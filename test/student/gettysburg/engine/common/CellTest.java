package student.gettysburg.engine.common;

import org.junit.Test;

import static gettysburg.common.Direction.*;
import static org.junit.Assert.assertEquals;

public class CellTest {

    @Test
    public void directionToSameCoordinate() {
        Cell cell = Cell.makeCell(1, 1);
        assertEquals(NONE, cell.directionTo(cell));
    }

    @Test
    public void directionToEastCoordinate() {
        Cell cell1 = Cell.makeCell(1, 1);
        Cell cell2 = Cell.makeCell(2, 1);
        assertEquals(EAST, cell1.directionTo(cell2));
    }

    @Test
    public void directionToWestCoordinate() {
        Cell cell1 = Cell.makeCell(2, 1);
        Cell cell2 = Cell.makeCell(1, 1);
        assertEquals(WEST, cell1.directionTo(cell2));
    }

    @Test
    public void directionToNorthCoordinate() {
        Cell cell1 = Cell.makeCell(1, 2);
        Cell cell2 = Cell.makeCell(1, 1);
        assertEquals(NORTH, cell1.directionTo(cell2));
    }

    @Test
    public void directionToSouthCoordinate() {
        Cell cell1 = Cell.makeCell(1, 1);
        Cell cell2 = Cell.makeCell(1, 2);
        assertEquals(SOUTH, cell1.directionTo(cell2));
    }

    @Test
    public void directionToNorthEastCoordinate() {
        Cell cell1 = Cell.makeCell(1, 2);
        Cell cell2 = Cell.makeCell(2, 1);
        assertEquals(NORTHEAST, cell1.directionTo(cell2));
    }

    @Test
    public void directionToNorthWestCoordinate() {
        Cell cell1 = Cell.makeCell(2, 2);
        Cell cell2 = Cell.makeCell(1, 1);
        assertEquals(NORTHWEST, cell1.directionTo(cell2));
    }

    @Test
    public void directionToSouthEastCoordinate() {
        Cell cell1 = Cell.makeCell(1, 1);
        Cell cell2 = Cell.makeCell(2, 2);
        assertEquals(SOUTHEAST, cell1.directionTo(cell2));
    }

    @Test
    public void directionToSouthWestCoordinate() {
        Cell cell1 = Cell.makeCell(2, 1);
        Cell cell2 = Cell.makeCell(1, 2);
        assertEquals(SOUTHWEST, cell1.directionTo(cell2));
    }

    @Test
    public void directionToNonCardinalCoordinate() {
        Cell cell1 = Cell.makeCell(1, 1);
        Cell cell2 = Cell.makeCell(4, 3);
        assertEquals(SOUTHEAST, cell1.directionTo(cell2));
    }

    @Test
    public void distanceToSameCoordinate() {
        Cell cell = Cell.makeCell(1, 1);
        assertEquals(0, cell.distanceTo(cell));
    }

    @Test
    public void distanceToEastCoordinate() {
        Cell cell1 = Cell.makeCell(1, 1);
        Cell cell2 = Cell.makeCell(2, 1);
        assertEquals(1, cell1.distanceTo(cell2));
    }

    @Test
    public void distanceToWestCoordinate() {
        Cell cell1 = Cell.makeCell(2, 1);
        Cell cell2 = Cell.makeCell(1, 1);
        assertEquals(1, cell1.distanceTo(cell2));
    }

    @Test
    public void distanceToSouthCoordinate() {
        Cell cell1 = Cell.makeCell(1, 1);
        Cell cell2 = Cell.makeCell(1, 2);
        assertEquals(1, cell1.distanceTo(cell2));
    }

    @Test
    public void distanceToNorthCoordinate() {
        Cell cell1 = Cell.makeCell(1, 2);
        Cell cell2 = Cell.makeCell(1, 1);
        assertEquals(1, cell1.distanceTo(cell2));
    }

    @Test
    public void distanceToNorthEastCoordinate() {
        Cell cell1 = Cell.makeCell(1, 2);
        Cell cell2 = Cell.makeCell(2, 1);
        assertEquals(1, cell1.distanceTo(cell2));
    }

    @Test
    public void distanceToNorthWestCoordinate() {
        Cell cell1 = Cell.makeCell(2, 2);
        Cell cell2 = Cell.makeCell(1, 1);
        assertEquals(1, cell1.distanceTo(cell2));
    }

    @Test
    public void distanceToSouthEastCoordinate() {
        Cell cell1 = Cell.makeCell(1, 1);
        Cell cell2 = Cell.makeCell(2, 2);
        assertEquals(1, cell1.distanceTo(cell2));
    }

    @Test
    public void distanceToSouthWestCoordinate() {
        Cell cell1 = Cell.makeCell(2, 1);
        Cell cell2 = Cell.makeCell(1, 2);
        assertEquals(1, cell1.distanceTo(cell2));
    }

    @Test
    public void distanceToNonCardinalCoordinate() {
        Cell cell1 = Cell.makeCell(1, 1);
        Cell cell2 = Cell.makeCell(4, 3);
        assertEquals(3, cell1.distanceTo(cell2));
    }
}