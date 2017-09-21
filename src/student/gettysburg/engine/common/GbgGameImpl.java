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

import java.util.*;
import java.util.stream.Collectors;

import com.sun.org.apache.xpath.internal.operations.Bool;
import gettysburg.common.*;
import gettysburg.common.exceptions.GbgInvalidActionException;
import gettysburg.common.exceptions.GbgInvalidMoveException;
import student.gettysburg.engine.utility.configure.BattleOrder;
import student.gettysburg.engine.utility.configure.UnitInitializer;

import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.GbgGameStatus.IN_PROGRESS;
import static gettysburg.common.GbgGameStatus.UNION_WINS;
import static gettysburg.common.GbgGameStep.*;
import static student.gettysburg.engine.common.CoordinateImpl.makeCoordinate;
import static student.gettysburg.engine.common.GbgUnitImpl.makeUnit;
import static student.gettysburg.engine.utility.configure.BattleOrder.getConfederateBattleOrder;
import static student.gettysburg.engine.utility.configure.BattleOrder.getUnionBattleOrder;

/**
 * This is the game engine master class that provides the interface to the game
 * implementation. DO NOT change the name of this file and do not change the
 * name of the methods that are defined here since they must be defined to implement the
 * GbgGame interface.
 * 
 * @version Jun 9, 2017
 */
public class GbgGameImpl implements GbgGame {

	private static final Integer TURN_LIMIT = 1;
	private Integer turnNumber = 1;
	private GbgGameStep currentStep = UMOVE;
	private GbgGameStatus gameStatus = IN_PROGRESS;
	private final Calendar gameDate;
	private final Map<GbgUnit, Coordinate> unitPositions;


	public GbgGameImpl() {
		unitPositions = new HashMap<>();
		gameDate = Calendar.getInstance();

		unitPositions.put(new GbgUnitImpl(getUnionBattleOrder()[0].unit), getUnionBattleOrder()[0].where);
		unitPositions.put(new GbgUnitImpl(getUnionBattleOrder()[1].unit), getUnionBattleOrder()[1].where);
		unitPositions.put(new GbgUnitImpl(getConfederateBattleOrder()[0].unit), getConfederateBattleOrder()[0].where);
	}

	/*
         * @see gettysburg.common.GbgGame#endBattleStep()
         */
	@Override
	public void endBattleStep() {
		if (currentStep == UBATTLE || currentStep == CBATTLE)
			endStep();
		else
			throw new GbgInvalidActionException("Tried to end battle step during move");
	}

	/*
	 * @see gettysburg.common.GbgGame#endMoveStep()
	 */
	@Override
	public void endMoveStep() {
		if (currentStep == UMOVE || currentStep == CMOVE)
			endStep();
		else
			throw new GbgInvalidActionException("Tried to end move step during battle");
	}

	/*
	 * @see gettysburg.common.GbgGame#endStep()
	 */
	@Override
	public GbgGameStep endStep() {
		switch (currentStep) {
			case UMOVE: currentStep = CMOVE; break;
			case CMOVE: currentStep = UBATTLE; break;
			case UBATTLE: currentStep = CBATTLE; break;
			case CBATTLE: currentStep = UMOVE; turnNumber++; break;
		}
		if (turnNumber > TURN_LIMIT)
			gameStatus = UNION_WINS;
		return currentStep;
	}

	/*
	 * @see gettysburg.common.GbgGame#getUnitFacing(int)
	 */
	@Override
	public Direction getUnitFacing(GbgUnit unit) {
		return getUnit(unit).getFacing();
	}

	/*
	 * @see gettysburg.common.GbgGame#getUnitsAt(gettysburg.common.Coordinate)
	 */
	@Override
	public Collection<GbgUnit> getUnitsAt(Coordinate where) {
		return unitPositions
				.entrySet()
				.stream()
				.filter(entry -> isUnitAtCoordinate(entry, where))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	private Boolean isUnitAtCoordinate(Map.Entry<GbgUnit, Coordinate> entry, Coordinate where) {
		return entry.getValue().getX() == where.getX() && entry.getValue().getY() == where.getY();
	}

	/*
	 * @see gettysburg.common.GbgGame#moveUnit(gettysburg.common.GbgUnit, gettysburg.common.Coordinate, gettysburg.common.Coordinate)
	 */
	@Override
	public void moveUnit(GbgUnit unit, Coordinate from, Coordinate to) {
		validateMove(unit, from, to);
		unitPositions.put(unit, to);
	}

	private void validateMove(GbgUnit unit, Coordinate from, Coordinate to) {
		if (!isAbleToMove(unit))
			throw new GbgInvalidActionException("Tried to move " + unit + " when " + currentStep);
		if (!from.equals(whereIsUnit(unit)))
			throw new GbgInvalidMoveException("Invalid FROM coordinate");
		Integer distance = from.distanceTo(to);
		if (distance > unit.getMovementFactor())
			throw new GbgInvalidMoveException("Tried to move " + unit + " " + distance + " squares");
		if (!getUnitsAt(to).isEmpty())
			throw new GbgInvalidMoveException("Tried to move " + unit + " to occupied square");
	}

	private Boolean isAbleToMove(GbgUnit unit) {
		GbgGameStep move = (unit.getArmy() == UNION) ? UMOVE : CMOVE;
		return  currentStep == move;
	}

	/*
	 * @see gettysburg.common.GbgGame#setUnitFacing(gettysburg.common.GbgUnit, gettysburg.common.Direction)
	 */
	@Override
	public void setUnitFacing(GbgUnit unit, Direction direction) {
		if (isAbleToMove(unit))
			getUnit(unit).setFacing(direction);
		else
			throw new GbgInvalidActionException("Tried to turn " + unit + " when " + currentStep);
	}

	private GbgUnit getUnit(GbgUnit unit) {
		Optional<GbgUnit> unitOptional = unitPositions
				.keySet()
				.stream()
				.filter(u -> u.equals(unit))
				.findFirst();
		if (unitOptional.isPresent())
			return unitOptional.get();
		throw new RuntimeException("Could not find unit: " + unit);
	}

	/*
	 * @see gettysburg.common.GbgGame#whereIsUnit(gettysburg.common.GbgUnit)
	 */
	@Override
	public Coordinate whereIsUnit(GbgUnit unit) {
		return unitPositions.get(unit);
	}

	/*
	 * @see gettysburg.common.GbgGame#whereIsUnit(java.lang.String, gettysburg.common.ArmyID)
	 */
	@Override
	public Coordinate whereIsUnit(String leader, ArmyID army)
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
		return turnNumber;
	}

}
