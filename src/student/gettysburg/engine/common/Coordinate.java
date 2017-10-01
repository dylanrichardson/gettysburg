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

import gettysburg.common.Direction;
import gettysburg.common.GbgBoard;
import gettysburg.common.exceptions.GbgInvalidCoordinateException;

import static gettysburg.common.Direction.*;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Implementation of the gettysburg.common.Coordinate interface. Additional methods
 * used in this implementation are added to this class. Clients should <em>ONLY</em>
 * use the public Coordinate interface. Additional methods
 * are only for engine internal use.
 * 
 * @version Jun 9, 2017
 */
public class Coordinate implements gettysburg.common.Coordinate
{
	private final int x, y;
	
	/**
	 * Private constructor that is called by the factory method.
	 * @param x
	 * @param y
	 */
	private Coordinate(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Needed for JSON processing.
	 */
	public Coordinate()
	{
		x = y = 0;
	}
	
	/**
	 * Factory method for creating Coordinates.
	 * @param x
	 * @param y
	 * @return
	 */
	public static Coordinate makeCoordinate(int x, int y)
	{
		if (x < 1 || x > GbgBoard.COLUMNS || y < 1 || y > GbgBoard.ROWS) {
			throw new GbgInvalidCoordinateException(
					"Coordinates for (" + x + ", " + y + ") are out of bounds.");
		}
		return new Coordinate(x, y);
	}

	/**
	 * Factory method for copying Coordinates.
	 * @param coordinate
	 * @return
	 */
	public static Coordinate makeCoordinate(gettysburg.common.Coordinate coordinate)
	{
		return makeCoordinate(coordinate.getX(), coordinate.getY());
	}
	
	/*
	 * @see gettysburg.common.Coordinate#directionTo(gettysburg.common.Coordinate)
	 */
	@Override
	public Direction directionTo(gettysburg.common.Coordinate coordinate) {
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
	 * @see gettysburg.common.Coordinate#distanceTo(gettysburg.common.Coordinate)
	 */
	@Override
	public int distanceTo(gettysburg.common.Coordinate coordinate) {
		return (int) sqrt(pow(coordinate.getX() - x, 2) + pow(coordinate.getY() - y, 2));
	}

	/*
	 * @see gettysburg.common.Coordinate#getX()
	 */
	@Override
	public int getX() {
		return x;
	}

	/*
	 * @see gettysburg.common.Coordinate#getY()
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
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	/*
	 * We do not compare a Coordinate to any object that just implements
	 * the Coordinate interface.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Coordinate)) {
			return false;
		}
		Coordinate other = (Coordinate) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return"(" + x + ", " + y + ")";
	}
}
