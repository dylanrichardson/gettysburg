package student.gettysburg.engine.common;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public enum Direction {
    NORTH(0, 1),
    NORTHEAST(1, 1),
    EAST(1, 0),
    SOUTHEAST(1, -1),
    SOUTH(0, -1),
    SOUTHWEST(-1, -1),
    WEST(-1, 0),
    NORTHWEST(-1, 1),
    NONE(0, 0);

    final Integer dx;
    final Integer dy;

    private static final Map<Cell, Direction> map = new HashMap<>();

    Direction(Integer dx, Integer dy) {
        this.dx = dx;
        this.dy = dy;
    }

    static {
        for (Direction dir : Direction.values()) {
            map.put(new Cell(dir.dx, dir.dy), dir);
        }
    }

    static Stream<Direction> all() {
        return Stream.concat(semiCircle(), semiCircle().map(Direction::opposite));
    }

    static Stream<Direction> semiCircle() {
        return Stream.of(NORTH, EAST, NORTHEAST, NORTHWEST);
    }

    static Direction fromOriginal(gettysburg.common.Direction direction) {
        if (direction == null)
            return null;
        return valueOf(direction.name());
    }

    private static Direction fromDelta(Integer dx, Integer dy) {
        return map.get(new Cell(dx, dy));
    }

    Direction opposite() {
        return fromDelta(-dx, -dy);
    }

    gettysburg.common.Direction getOriginal() {
        return gettysburg.common.Direction.valueOf(name());
    }

    Direction rotateClockwise() {
        return rotate(1);
    }

    Direction rotateCounterClockwise() {
        return rotate(-1);
    }

    Direction rotate(Integer delta) {
        // TODO convert to p
        if (delta == 0) {
            return fromDelta(dx, dy);
        }
        if (isCardinal()) {
            return fromDelta(dx + delta * dy, dy - delta * dx);
        }
        if (dx * dy == delta) {
            return fromDelta(dx, 0);
        }
        return fromDelta(0, dy);
    }

    private Boolean isCardinal() {
        return asList(NORTH, EAST, SOUTH, WEST).contains(this);
    }
}
