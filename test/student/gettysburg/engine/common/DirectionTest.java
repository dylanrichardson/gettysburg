package student.gettysburg.engine.common;

import org.junit.Test;

import static org.junit.Assert.*;
import static student.gettysburg.engine.common.Direction.*;

public class DirectionTest {

    @Test
    public void rotateClockwiseNorth() {
        assertEquals(NORTHEAST, NORTH.rotateClockwise());
    }

    @Test
    public void rotateClockwiseNorthEast() {
        assertEquals(EAST, NORTHEAST.rotateClockwise());
    }

    @Test
    public void rotateClockwiseEast() {
        assertEquals(SOUTHEAST, EAST.rotateClockwise());
    }

    @Test
    public void rotateClockwiseSouthEast() {
        assertEquals(SOUTH, SOUTHEAST.rotateClockwise());
    }

    @Test
    public void rotateClockwiseSouth() {
        assertEquals(SOUTHWEST, SOUTH.rotateClockwise());
    }

    @Test
    public void rotateClockwiseSouthWest() {
        assertEquals(WEST, SOUTHWEST.rotateClockwise());
    }

    @Test
    public void rotateClockwiseWest() {
        assertEquals(NORTHWEST, WEST.rotateClockwise());
    }

    @Test
    public void rotateClockwiseNorthWest() {
        assertEquals(NORTH, NORTHWEST.rotateClockwise());
    }

    @Test
    public void rotateCounterClockwiseNorth() {
        assertEquals(NORTHWEST, NORTH.rotateCounterClockwise());
    }

    @Test
    public void rotateCounterClockwiseNorthEast() {
        assertEquals(NORTH, NORTHEAST.rotateCounterClockwise());
    }

    @Test
    public void rotateCounterClockwiseEast() {
        assertEquals(NORTHEAST, EAST.rotateCounterClockwise());
    }

    @Test
    public void rotateCounterClockwiseSouthEast() {
        assertEquals(EAST, SOUTHEAST.rotateCounterClockwise());
    }

    @Test
    public void rotateCounterClockwiseSouth() {
        assertEquals(SOUTHEAST, SOUTH.rotateCounterClockwise());
    }

    @Test
    public void rotateCounterClockwiseSouthWest() {
        assertEquals(SOUTH, SOUTHWEST.rotateCounterClockwise());
    }

    @Test
    public void rotateCounterClockwiseWest() {
        assertEquals(SOUTHWEST, WEST.rotateCounterClockwise());
    }

    @Test
    public void rotateCounterClockwiseNorthWest() {
        assertEquals(WEST, NORTHWEST.rotateCounterClockwise());
    }

}