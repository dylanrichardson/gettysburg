package student.gettysburg.engine.common;

import org.junit.Test;

import static gettysburg.common.Direction.*;
import static org.junit.Assert.assertEquals;

public class CoordinateTest {

    @Test
    public void directionToSameCoordinate() {
        Coordinate coordinate = Coordinate.makeCoordinate(1, 1);
        assertEquals(NONE, coordinate.directionTo(coordinate));
    }

    @Test
    public void directionToEastCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(1, 1);
        Coordinate coordinate2 = Coordinate.makeCoordinate(2, 1);
        assertEquals(EAST, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToWestCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(2, 1);
        Coordinate coordinate2 = Coordinate.makeCoordinate(1, 1);
        assertEquals(WEST, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToNorthCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(1, 2);
        Coordinate coordinate2 = Coordinate.makeCoordinate(1, 1);
        assertEquals(NORTH, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToSouthCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(1, 1);
        Coordinate coordinate2 = Coordinate.makeCoordinate(1, 2);
        assertEquals(SOUTH, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToNorthEastCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(1, 2);
        Coordinate coordinate2 = Coordinate.makeCoordinate(2, 1);
        assertEquals(NORTHEAST, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToNorthWestCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(2, 2);
        Coordinate coordinate2 = Coordinate.makeCoordinate(1, 1);
        assertEquals(NORTHWEST, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToSouthEastCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(1, 1);
        Coordinate coordinate2 = Coordinate.makeCoordinate(2, 2);
        assertEquals(SOUTHEAST, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToSouthWestCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(2, 1);
        Coordinate coordinate2 = Coordinate.makeCoordinate(1, 2);
        assertEquals(SOUTHWEST, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToNonCardinalCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(1, 1);
        Coordinate coordinate2 = Coordinate.makeCoordinate(4, 3);
        assertEquals(SOUTHEAST, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void distanceToSameCoordinate() {
        Coordinate coordinate = Coordinate.makeCoordinate(1, 1);
        assertEquals(0, coordinate.distanceTo(coordinate));
    }

    @Test
    public void distanceToEastCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(1, 1);
        Coordinate coordinate2 = Coordinate.makeCoordinate(2, 1);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToWestCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(2, 1);
        Coordinate coordinate2 = Coordinate.makeCoordinate(1, 1);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToSouthCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(1, 1);
        Coordinate coordinate2 = Coordinate.makeCoordinate(1, 2);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToNorthCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(1, 2);
        Coordinate coordinate2 = Coordinate.makeCoordinate(1, 1);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToNorthEastCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(1, 2);
        Coordinate coordinate2 = Coordinate.makeCoordinate(2, 1);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToNorthWestCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(2, 2);
        Coordinate coordinate2 = Coordinate.makeCoordinate(1, 1);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToSouthEastCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(1, 1);
        Coordinate coordinate2 = Coordinate.makeCoordinate(2, 2);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToSouthWestCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(2, 1);
        Coordinate coordinate2 = Coordinate.makeCoordinate(1, 2);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToNonCardinalCoordinate() {
        Coordinate coordinate1 = Coordinate.makeCoordinate(1, 1);
        Coordinate coordinate2 = Coordinate.makeCoordinate(4, 3);
        assertEquals(3, coordinate1.distanceTo(coordinate2));
    }
}