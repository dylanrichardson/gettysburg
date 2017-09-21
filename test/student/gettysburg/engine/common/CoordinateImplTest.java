package student.gettysburg.engine.common;

import org.junit.Test;

import static gettysburg.common.Direction.*;
import static org.junit.Assert.assertEquals;

public class CoordinateImplTest {

    @Test
    public void directionToSameCoordinate() {
        CoordinateImpl coordinate = CoordinateImpl.makeCoordinate(1, 1);
        assertEquals(NONE, coordinate.directionTo(coordinate));
    }

    @Test
    public void directionToEastCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(1, 1);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(2, 1);
        assertEquals(EAST, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToWestCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(2, 1);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(1, 1);
        assertEquals(WEST, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToNorthCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(1, 2);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(1, 1);
        assertEquals(NORTH, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToSouthCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(1, 1);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(1, 2);
        assertEquals(SOUTH, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToNorthEastCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(1, 2);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(2, 1);
        assertEquals(NORTHEAST, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToNorthWestCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(2, 2);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(1, 1);
        assertEquals(NORTHWEST, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToSouthEastCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(1, 1);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(2, 2);
        assertEquals(SOUTHEAST, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToSouthWestCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(2, 1);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(1, 2);
        assertEquals(SOUTHWEST, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void directionToNonCardinalCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(1, 1);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(4, 3);
        assertEquals(SOUTHEAST, coordinate1.directionTo(coordinate2));
    }

    @Test
    public void distanceToSameCoordinate() {
        CoordinateImpl coordinate = CoordinateImpl.makeCoordinate(1, 1);
        assertEquals(0, coordinate.distanceTo(coordinate));
    }

    @Test
    public void distanceToEastCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(1, 1);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(2, 1);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToWestCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(2, 1);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(1, 1);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToSouthCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(1, 1);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(1, 2);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToNorthCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(1, 2);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(1, 1);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToNorthEastCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(1, 2);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(2, 1);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToNorthWestCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(2, 2);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(1, 1);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToSouthEastCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(1, 1);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(2, 2);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToSouthWestCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(2, 1);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(1, 2);
        assertEquals(1, coordinate1.distanceTo(coordinate2));
    }

    @Test
    public void distanceToNonCardinalCoordinate() {
        CoordinateImpl coordinate1 = CoordinateImpl.makeCoordinate(1, 1);
        CoordinateImpl coordinate2 = CoordinateImpl.makeCoordinate(4, 3);
        assertEquals(3, coordinate1.distanceTo(coordinate2));
    }
}