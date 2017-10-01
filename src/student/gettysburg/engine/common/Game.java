/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2016-2017 Gary F. Pollice
 *******************************************************************************/
package student.gettysburg.engine.common;

import gettysburg.common.*;
import gettysburg.common.exceptions.GbgInvalidActionException;
import gettysburg.common.exceptions.GbgInvalidMoveException;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.GbgGameStatus.IN_PROGRESS;
import static gettysburg.common.GbgGameStatus.UNION_WINS;
import static gettysburg.common.GbgGameStep.*;
import static java.util.Arrays.asList;
import static student.gettysburg.engine.common.Coordinate.makeCoordinate;
import static student.gettysburg.engine.common.Reinforcements.makeReinforcements;
import static student.gettysburg.engine.common.Unit.makeUnit;
import static student.gettysburg.engine.utility.configure.BattleOrder.getBattleOrder;

/**
 * This is the game engine master class that provides the interface to the game
 * implementation. DO NOT change the name of this file and do not change the
 * name of the methods that are defined here since they must be defined to implement the
 * GbgGame interface.
 * 
 * @version Jun 9, 2017
 */
public class Game implements GbgGame {

	private static final Integer TURN_LIMIT = 49;

	private final Calendar gameDate = Calendar.getInstance();
	private GbgGameStatus gameStatus = IN_PROGRESS;
	private Reinforcements reinforcements = makeReinforcements(getBattleOrder());

	Integer currentTurn = 1;
	GbgGameStep currentStep = UMOVE; // the step before first (UMOVE)
	Board board = new Board();


	public Game() {
		placeReinforcements(UNION, 0);
		placeReinforcements(CONFEDERATE, 0);
	}

	/*
	 * @see gettysburg.common.GbgGame#endStep()
	 */
	@Override
	public GbgGameStep endStep() {
		board.removeStackedUnits();

		if (isLastStepInTurn(currentStep))
			endTurn();

		currentStep = getNextStep(currentStep);
		if (isMoveStep(currentStep))
			placeReinforcements();

		return currentStep;
	}

	@Override
	public Collection<BattleDescriptor> getBattlesToResolve() {
		// TODO
		return Collections.emptyList();
	}

	@Override
	public BattleResolution resolveBattle(BattleDescriptor battle) {
		return null;
	}

	/*
	 * @see gettysburg.common.GbgGame#getUnitFacing(int)
	 */
	@Override
	public Direction getUnitFacing(GbgUnit unit) {
		return board.getUnit(unit).getFacing();
	}

	/*
	 * @see gettysburg.common.GbgGame#getUnitsAt(gettysburg.common.Coordinate)
	 */
	@Override
	public Collection<GbgUnit> getUnitsAt(gettysburg.common.Coordinate coordinate) {
		Collection<GbgUnit> units = board.getUnitsAt(coordinate);
		if (units.isEmpty())
			return null;
		return units;
	}

	/*
	 * @see gettysburg.common.GbgGame#moveUnit(gettysburg.common.GbgUnit, gettysburg.common.Coordinate, gettysburg.common.Coordinate)
	 */
	@Override
	public void moveUnit(GbgUnit unit, gettysburg.common.Coordinate from, gettysburg.common.Coordinate to) {
		validateMove(unit, from, to);
		board.moveUnit(unit, to);
	}

	/*
	 * @see gettysburg.common.GbgGame#setUnitFacing(gettysburg.common.GbgUnit, gettysburg.common.Direction)
	 */
	@Override
	public void setUnitFacing(GbgUnit unit, Direction direction) {
		if (isAbleToMove(unit))
			board.getUnit(unit).setFacing(direction);
		else
			throw new GbgInvalidActionException("Tried to turn " + unit + " when " + currentStep);
	}

	@Override
	public GbgUnit getUnit(String leader, ArmyID armyID) {
		return board.getUnit(makeUnit(armyID, leader));
	}

	/*
	 * @see gettysburg.common.GbgGame#whereIsUnit(gettysburg.common.GbgUnit)
	 */
	@Override
	public gettysburg.common.Coordinate whereIsUnit(GbgUnit unit) {
		return board.getUnitPosition(unit);
	}

	/*
	 * @see gettysburg.common.GbgGame#whereIsUnit(java.lang.String, gettysburg.common.ArmyID)
	 */
	@Override
	public gettysburg.common.Coordinate whereIsUnit(String leader, ArmyID army)
	{
		return whereIsUnit(makeUnit(army, 0, null, leader, 0, null, null));
	}

	/*
	 * @see gettysburg.common.GbgGame#getCurrentStep()
	 */
	@Override
	public GbgGameStep getCurrentStep() {
		return currentStep;
	}

	/*
	 * @see gettysburg.common.GbgGame#getGameStatus()
	 */
	@Override
	public GbgGameStatus getGameStatus() {
		return gameStatus;
	}

	/*
	 * @see gettysburg.common.GbgGame#getGameDate()
	 */
	@Override
	public Calendar getGameDate() {
		return gameDate;
	}

	/*
	 * @see gettysburg.common.GbgGame#getTurnNumber()
	 */
	@Override
	public int getTurnNumber() {
		return currentTurn;
	}



	private void validateMove(GbgUnit unit, gettysburg.common.Coordinate fromCoord, gettysburg.common.Coordinate toCoord) {
		Coordinate from = makeCoordinate(fromCoord);
		Coordinate to = makeCoordinate(toCoord);
		if (!isAbleToMove(unit))
			throw new GbgInvalidActionException("Tried to move " + unit + " when " + currentStep);
		if (!from.equals(whereIsUnit(unit)))
			throw new GbgInvalidMoveException("Invalid FROM coordinate");
		Integer distance = from.distanceTo(to);
		if (distance > unit.getMovementFactor())
			throw new GbgInvalidMoveException("Tried to move " + unit + " " + distance + " squares");
		if (board.cellIsOccupied(to))
			throw new GbgInvalidMoveException("Tried to move " + unit + " to occupied square");
	}

	private void placeReinforcements() {
		placeReinforcements(getCurrentArmyID(), currentTurn);
	}

	private void placeReinforcements(ArmyID armyID, Integer turn) {
		reinforcements
				.get(armyID, turn)
				.forEach(unitInitializer -> board.placeUnit(unitInitializer));
	}

	private void endTurn() {
		Integer nextTurn = getNextTurn(currentTurn);
		if (isLastTurnInGame(nextTurn))
			endGame();
		else
			currentTurn = nextTurn;
	}

	private void endGame() {
		gameStatus = UNION_WINS;
	}

	private Boolean isLastTurnInGame(Integer turn) {
		return turn > TURN_LIMIT;
	}

	private Integer getNextTurn(Integer turn) {
		return turn + 1;
	}

	private Boolean isLastStepInTurn(GbgGameStep step) {
		return step == CBATTLE;
	}

	private ArmyID getCurrentArmyID() {
		switch (currentStep) {
			case UMOVE:
			case UBATTLE:
				return UNION;
			case CMOVE:
			case CBATTLE:
				return CONFEDERATE;
			default:
				return null;
		}
	}

	private Boolean isMoveStep(GbgGameStep step) {
		return step == UMOVE || step == CMOVE;
	}

	private GbgGameStep getNextStep(GbgGameStep step) {
		switch (step) {
			case UMOVE:
				return UBATTLE;
			case UBATTLE:
				return CMOVE;
			case CMOVE:
				return CBATTLE;
			case CBATTLE:
				return UMOVE;
			default:
				return null;
		}
	}

	private Boolean isAbleToMove(GbgUnit unit) {
		GbgGameStep move = (unit.getArmy() == UNION) ? UMOVE : CMOVE;
		return currentStep == move;
	}
}
