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
package student.gettysburg.engine;

import java.util.Collection;
import gettysburg.common.*;
import gettysburg.common.exceptions.GbgNotImplementedException;
import org.junit.Test;
import student.gettysburg.engine.common.CoordinateImpl;
import student.gettysburg.engine.common.GbgGameImpl;

import static junit.framework.TestCase.assertEquals;

/**
 * A factory class that contains creation methods for all of the components of
 * the Gettysburg game, including the game itself.
 * @version Jul 30, 2017
 */
public final class GettysburgFactory
{
	/**
	 * Creation method for a new Gettysburg game instance.
	 * @return a new Gettysburg game
	 */
	public static GbgGame makeGame()
	{
		return new GbgGameImpl();
	}
	
	/**
	 * Creation for a new Gettysburg game instance. In this case the
	 * client can specify a version, such as "alpha", or some other
	 * string that indicates that a version of the game with limited
	 * or extended capabilities should be produced. The valid version
	 * strings are defined by the instructor for the course. If the
	 * string is invalid, throw a GbgNotImplementedException.
	 * 
	 * @param version the version to create
	 * @return an instance of the specific version of the Gettysburg game
	 */
	public static GbgGame makeGame(String version)
	{
		throw new GbgNotImplementedException("makeGame()");
	}
	
	/**
	 * Factory method for creating Coordinates. This method makes a Coordinate
	 * instance that is used by the GbgGameImpl implementation and internal
	 * to the engine's classes. There is no requirement by the client that objects
	 * of this type are used to implement the Coordinate interface.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the Coordinate object as implemented by the Gettysburg engine.
	 */
	public static Coordinate makeCoordinate(int x, int y)
	{
		return CoordinateImpl.makeCoordinate(x, y);
	}
}
