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
import java.util.stream.Stream;

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

	private GbgGameStatus gameStatus = IN_PROGRESS;
	private final Reinforcements reinforcements = makeReinforcements(getBattleOrder());

	Integer currentTurn = 1;
	GbgGameStep currentStep = UMOVE;
	final Board board = new Board();
	private final Set<GbgUnit> movedUnits = new HashSet<>();
	private final Set<GbgUnit> rotatedUnits = new HashSet<>();
	private final Set<GbgUnit> battledUnits = new HashSet<>();
	private Collection<BattleDescriptor> battlesToResolve = emptyList();

	public Game() {
		placeReinforcements(UNION, 0);
		placeReinforcements(CONFEDERATE, 0);
	}

	@Override
	public GbgGameStep endStep() {
		if (isBattleStep(currentStep) && !battlesToResolve.isEmpty())
			throw new GbgInvalidActionException("Must resolve all battles before ending step");

		board.removeStackedUnits();

		if (isLastStepInTurn(currentStep))
			endTurn();

		currentStep = getNextStep(currentStep);

		if (isMoveStep(currentStep))
			placeReinforcements();

		if (isBattleStep(currentStep))
			battlesToResolve = getBattlesToResolve();

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
		BattleDescriptor battle = getBattleToResolve();
		if (battle == null)
			return emptyList();
		return singletonList(battle);
	}

	private BattleDescriptor getBattleToResolve() {
		// get units who need to battle still split by attackers/defenders
		Stream<GbgUnit> units = board
				.getUnitsInBattlePositions();
		Map<Boolean, List<GbgUnit>> unitMap = units
				.filter(hasBattled.negate())
				.collect(Collectors.groupingBy(this::isTurnToAttack, Collectors.toList()));
		if (unitMap.isEmpty())
			return null;
		return makeBattle(unitMap.get(true), unitMap.get(false));
	}

	@Override
	public BattleResolution resolveBattle(BattleDescriptor battleDescriptor) {
		validatePartialBattle(battleDescriptor);
		// track battled units
		battledUnits.addAll(battleDescriptor.getAttackers());
		battledUnits.addAll(battleDescriptor.getDefenders());
		Resolution resolution = getResolution(battleDescriptor);
		// move retreating units
		if (resolution.isRetreat())
			retreatUnits(resolution.getRetreatingUnits());
		// remove eliminated units
		board.removeUnits(resolution.getEliminatedUnionUnits());
		board.removeUnits(resolution.getEliminatedConfederateUnits());
		return resolution;
	}

	Resolution getResolution(BattleDescriptor battleDescriptor) {
		return makeResolution(battleDescriptor);
	}

	private void retreatUnits(Collection<Unit> units) {
		units.forEach(unit -> {
			Iterator<Cell> cell = board.getRetreatableSquares(unit).iterator();
			if (cell.hasNext())
				board.moveUnit(unit, cell.next());
			else
				board.removeUnit(unit);
		});
	}

	private void validatePartialBattle(BattleDescriptor partialBattle) {
		BattleDescriptor battle = getBattleToResolve();
		if (battle == null)
			throw invalidBattle("No battles to resolve");
		validateBattleUnits(partialBattle.getAttackers(), battle.getAttackers(), "attack");
		validateBattleUnits(partialBattle.getDefenders(), battle.getDefenders(), "defend");
	}

	private void validateBattleUnits(Collection<GbgUnit> unitsToValidate, Collection<GbgUnit> validUnits,
									 String verb) {
		unitsToValidate.forEach(unit -> {
			if (!validUnits.contains(unit)) {
				throw invalidBattle(unit + " cannot " + verb);
			}
		});
	}

	private GbgInvalidActionException invalidBattle(String msg) {
		return new GbgInvalidActionException("Invalid battle descriptor: " + msg);
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
		// increment only if not game over
		if (gameStatus == IN_PROGRESS)
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

	private final Predicate<GbgUnit> hasMoved = (unit) -> movedUnits.contains(unit);

	private final Predicate<GbgUnit> hasRotated = (unit) -> rotatedUnits.contains(unit);

	private final Predicate<GbgUnit> hasBattled = (unit) -> battledUnits.contains(unit);

	private Boolean isTurnToMove(GbgUnit unit) {
		GbgGameStep move = (unit.getArmy() == UNION) ? UMOVE : CMOVE;
		return currentStep == move;
	}

	private Boolean isTurnToAttack(GbgUnit unit) {
		GbgGameStep move = (unit.getArmy() == UNION) ? UBATTLE : CBATTLE;
		return currentStep == move;
	}
}
