/**
 * This A* algorithm has been adapted from https://github.com/ateamwpi/HospitalKiosk/blob/master/src/models/path/algo/AStar.java
 */
package student.gettysburg.engine.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;

class PathFinder {

    static Boolean find(Cell start, Cell end, Integer maxPathLength, Function<Cell, Collection<Cell>> getConnections) {
        LinkedList<Cell> closedSet = new LinkedList<>();
        LinkedList<Cell> openSet  = new LinkedList<>();
        HashMap<Cell, Cell> cameFrom  = new HashMap<>();
        HashMap<Cell, Double> gScore  = new HashMap<>();
        HashMap<Cell, Double> fScore  = new HashMap<>();

        openSet.add(start);
        fScore.put(start, (double) start.distanceTo(end));
        gScore.put(start, 0.0);

        while(!openSet.isEmpty()){
            // choose next best node in open set
            Cell current = openSet.getFirst();
            for (Cell n : openSet) {
                if (fScore.get(n) < fScore.get(current)) {
                    current = n;
                }
            }

            // goal check
            if(current.equals(end)) {
                return true;
            }
            openSet.remove(current);
            closedSet.add(current);

            // max length check
            Integer pathLen = pathLength(cameFrom, current);
            if (pathLen > maxPathLength)
                continue; // do not look for neighbors

            Collection<Cell> connections = getConnections.apply(current);
            for(Cell cell: connections) {
                if(closedSet.contains(cell))
                    continue;

                Double score = gScore.get(current) + current.distanceTo(cell);
                if(!openSet.contains(cell))
                    openSet.add(cell);
                else if(score >= gScore.get(cell))
                    continue;

                cameFrom.put(cell, current);
                gScore.put(cell, score);
                fScore.put(cell, score + cell.distanceTo(end));
            }
        }
        return false;
    }

    private static Integer pathLength(HashMap<Cell, Cell> cameFrom, Cell end) {
        Cell current = end;
        Integer length = 0;
        while(cameFrom.keySet().contains(current)){
            current = cameFrom.get(current);
            length++;
        }
        return length;
    }
}
