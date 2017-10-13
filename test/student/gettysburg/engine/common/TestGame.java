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

import java.util.Iterator;
import java.util.List;

import static java.util.Collections.emptyIterator;
import static student.gettysburg.engine.common.Cell.makeCell;
import static student.gettysburg.engine.common.Resolution.makeResolution;
import static student.gettysburg.engine.common.Unit.makeUnit;

/**
 * Test implementation of the Gettysburg game.
 * @version Jul 31, 2017
 */
public class TestGame extends Game implements TestGbgGame {

	private Iterator<BattleResult> battleResults = emptyIterator();

	@Override
	public void clearBoard() {
		board.clear();
	}

	@Override
	public void putUnitAt(GbgUnit unit, int x, int y, gettysburg.common.Direction facing) {
		board.moveUnit(makeUnit(unit), makeCell(x, y));
		board.getUnit(unit).setFacing(facing);
	}

	@Override
	public void setGameStep(GbgGameStep step) {
		currentStep = step;
	}

	@Override
	public void setGameTurn(int turn) {
		currentTurn = turn;
	}

	@Override
	public void setBattleResults(List<BattleResult> battleResults) {
		this.battleResults = battleResults.iterator();
	}

	@Override
	public Resolution getResolution(BattleDescriptor battleDescriptor) {
		if (!battleResults.hasNext())
			return super.getResolution(battleDescriptor);
		return makeResolution(battleDescriptor, battleResults.next());
	}
}
