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

import gettysburg.common.Coordinate;
import gettysburg.common.GbgBoard;
import gettysburg.common.exceptions.GbgInvalidCoordinateException;

import static gettysburg.common.Direction.*;
import static java.lang.Math.*;

/**
 * Implementation of the gettysburg.common.Coordinate interface. Additional methods
 * used in this implementation are added to this class. Clients should <em>ONLY</em>
 * use the public Cell interface. Additional methods
 * are only for engine internal use.
 * 
 * @version Jun 9, 2017
 */
public class Cell implements Coordinate
{
	private final int x, y;
	
	/**
	 * Private constructor that is called by the factory method.
	 * @param x
	 * @param y
	 */
	Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Needed for JSON processing.
	 */
	public Cell()
	{
		x = y = 0;
	}
	
	/**
	 * Factory method for creating Cells.
	 * @param x
	 * @param y
	 * @return
	 */
	public static Cell makeCell(int x, int y) {
		if (x < 1 || x > GbgBoard.COLUMNS || y < 1 || y > GbgBoard.ROWS) {
			throw new GbgInvalidCoordinateException("Coordinates for (" + x + ", " + y + ") are out of bounds.");
		}
		return new Cell(x, y);
	}

	/**
	 * Factory method for copying Coordinates.
	 * @param coordinate
	 * @return
	 */
	public static Cell makeCell(Coordinate coordinate)
	{
		return makeCell(coordinate.getX(), coordinate.getY());
	}
	
	/*
	 * @see gettysburg.common.Cell#directionTo(gettysburg.common.Cell)
	 */
	@Override
	public gettysburg.common.Direction directionTo(Coordinate coordinate) {
		if (coordinate.getX() > x) {
			if (coordinate.getY() < y) {
				return NORTHEAST;
			}
			if (coordinate.getY() > y) {
				return SOUTHEAST;
			}
			return EAST;
		}
		if (coordinate.getX() < x) {
			if (coordinate.getY() < y) {
				return NORTHWEST;
			}
			if (coordinate.getY() > y) {
				return SOUTHWEST;
			}
			return WEST;
		}
		if (coordinate.getY() < y)
			return NORTH;
		if (coordinate.getY() > y)
			return SOUTH;
		return NONE;
	}

	/*
	 * @see gettysburg.common.Cell#distanceTo(gettysburg.common.Cell)
	 */
	@Override
	public int distanceTo(Coordinate coordinate) {
		return max(abs(x - coordinate.getX()), abs(y - coordinate.getY()));
	}

	/*
	 * @see gettysburg.common.Cell#getX()
	 */
	@Override
	public int getX() {
		return x;
	}

	/*
	 * @see gettysburg.common.Cell#getY()
	 */
	@Override
	public int getY() {
		return y;
	}
	
	// Change the equals and hashCode if you need to.
	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	/*
	 * We do not compare a Cell to any object that just implements
	 * the Cell interface.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Cell)) {
			return false;
		}
		Cell other = (Cell) obj;
		return x == other.x && y == other.y;
	}

	@Override
	public String toString()
	{
		return"(" + x + ", " + y + ")";
	}

	Cell getAdjacent(Direction direction) {
		return new Cell(x + direction.dx, y + direction.dy);
	}
}
