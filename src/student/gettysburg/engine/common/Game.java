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
import gettysburg.common.Direction;
import gettysburg.common.exceptions.GbgInvalidActionException;
import gettysburg.common.exceptions.GbgInvalidMoveException;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.GbgGameStatus.IN_PROGRESS;
import static gettysburg.common.GbgGameStatus.UNION_WINS;
import static gettysburg.common.GbgGameStep.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static student.gettysburg.engine.common.Battle.makeBattle;
import static student.gettysburg.engine.common.Cell.makeCell;
import static student.gettysburg.engine.common.Reinforcements.makeReinforcements;
import static student.gettysburg.engine.common.Resolution.makeResolution;
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
	private Set<GbgUnit> movedUnits = new HashSet<>();
	private Set<GbgUnit> rotatedUnits = new HashSet<>();
	private Set<GbgUnit> battledUnits = new HashSet<>();

	public Game() {
		placeReinforcements(UNION, 0);
		placeReinforcements(CONFEDERATE, 0);
	}

	@Override
	public GbgGameStep endStep() {
		if (isBattleStep(currentStep) && !getBattlesToResolve().isEmpty())
			throw new GbgInvalidActionException("Must resolve all battles before ending step");

		board.removeStackedUnits();

		if (isLastStepInTurn(currentStep))
			endTurn();

		currentStep = getNextStep(currentStep);
		if (isMoveStep(currentStep))
			placeReinforcements();

		return currentStep;
	}

	@Override
	public void moveUnit(GbgUnit unit, Coordinate from, Coordinate to) {
		validateMove(unit, from, to);
		board.moveUnit(makeUnit(unit), makeCell(to));
		movedUnits.add(unit);
	}

	@Override
	public void setUnitFacing(GbgUnit unit, Direction direction) {
		validateRotation(unit);
		board.getUnit(unit).setFacing(direction);
		rotatedUnits.add(unit);
	}

	@Override
	public Collection<BattleDescriptor> getBattlesToResolve() {
		// get units who need to battle still and split by army
		Map<Boolean, List<GbgUnit>> unitMap = board
				.getUnitsInBattlePositions()
				.filter(hasBattled.negate())
				.collect(Collectors.groupingBy(this::isTurnToAttack, Collectors.toList()));
		if (unitMap.isEmpty())
			return emptyList();
		return singletonList(makeBattle(unitMap.get(true), unitMap.get(false)));
	}

	@Override
	public BattleResolution resolveBattle(BattleDescriptor battleDescriptor) {
		battledUnits.addAll(battleDescriptor.getAttackers());
		battledUnits.addAll(battleDescriptor.getDefenders());
		Resolution resolution = makeResolution(battleDescriptor);
		board.removeUnits(resolution.getEliminatedUnionUnits());
		board.removeUnits(resolution.getEliminatedConfederateUnits());
		return resolution;
	}

	@Override
	public Direction getUnitFacing(GbgUnit unit) {
		return board.getUnit(unit).getFacing();
	}

	@Override
	public Collection<GbgUnit> getUnitsAt(Coordinate coordinate) {
		Collection<GbgUnit> units = board.getUnitsAt(makeCell(coordinate));
		if (units.isEmpty())
			return null;
		return units;
	}

	@Override
	public GbgUnit getUnit(String leader, ArmyID armyID) {
		return board.getUnit(makeUnit(armyID, leader));
	}

	@Override
	public Coordinate whereIsUnit(GbgUnit unit) {
		return board.getUnitPosition(makeUnit(unit));
	}

	@Override
	public Coordinate whereIsUnit(String leader, ArmyID army)
	{
		return whereIsUnit(makeUnit(army, 0, null, leader, 0, null, null));
	}

	@Override
	public GbgGameStep getCurrentStep() {
		return currentStep;
	}

	@Override
	public GbgGameStatus getGameStatus() {
		return gameStatus;
	}

	@Override
	public Calendar getGameDate() {
		return gameDate;
	}

	@Override
	public int getTurnNumber() {
		return currentTurn;
	}

	// private

	private void validateMove(GbgUnit unit, Coordinate fromCoord, Coordinate toCoord) {
		Cell from = makeCell(fromCoord);
		Cell to = makeCell(toCoord);
		if (!isTurnToMove(unit))
			throw new GbgInvalidMoveException("Tried to move " + unit + " when " + currentStep);
		if (hasMoved.test(unit))
			throw new GbgInvalidMoveException("Tried to move " + unit + " twice in turn");
		if (!from.equals(whereIsUnit(unit)))
			throw new GbgInvalidMoveException("Invalid FROM coordinate");
		Integer distance = from.distanceTo(to);
		if (distance > unit.getMovementFactor())
			throw new GbgInvalidMoveException("Tried to move " + unit + " " + distance + " squares");
		if (board.cellIsOccupied.test(to))
			throw new GbgInvalidMoveException("Tried to move " + unit + " to occupied square");
		if (!board.hasPath(makeUnit(unit), to, unit.getMovementFactor()))
			throw new GbgInvalidMoveException("Could not find a valid path to destination");
	}

	private void validateRotation(GbgUnit unit) {
		if (!isTurnToMove(unit))
			throw new GbgInvalidMoveException("Tried to turn " + unit + " when " + currentStep);
		if (hasRotated.test(unit))
			throw new GbgInvalidMoveException("Tried to rotate " + unit + " twice in turn");
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
		movedUnits.clear();
		rotatedUnits.clear();
		battledUnits.clear();
		incrementTurn();
	}

	private void incrementTurn() {
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

	private Boolean isBattleStep(GbgGameStep step) {
		return !isMoveStep(step);
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

	private Predicate<GbgUnit> hasMoved = (unit) -> movedUnits.contains(unit);

	private Predicate<GbgUnit> hasRotated = (unit) -> rotatedUnits.contains(unit);

	private Predicate<GbgUnit> hasBattled = (unit) -> battledUnits.contains(unit);

	private Boolean isTurnToMove(GbgUnit unit) {
		GbgGameStep move = (unit.getArmy() == UNION) ? UMOVE : CMOVE;
		return currentStep == move;
	}

	private Boolean isTurnToAttack(GbgUnit unit) {
		GbgGameStep move = (unit.getArmy() == UNION) ? UBATTLE : CBATTLE;
		return currentStep == move;
	}
}
