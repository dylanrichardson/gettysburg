package student.gettysburg.engine.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;

class PathFinder {

    static Boolean find(Cell start, Cell end, Integer length, Function<Cell, Collection<Cell>> getConnections) {

        // TODO FIX
        return true;


//        LinkedList<Cell> closedSet = new LinkedList<>();
//        LinkedList<Cell> openSet  = new LinkedList<>();
//        HashMap<Cell, Cell> cameFrom  = new HashMap<>();
//        HashMap<Cell, Double> gScore  = new HashMap<>();
//        HashMap<Cell, Double> fScore  = new HashMap<>();
//
//        openSet.add(start);
//        fScore.put(start, (double) start.distanceTo(end));
//        gScore.put(start, 0.0);
//
//        while(!openSet.isEmpty()){
//            Cell current = openSet.getFirst();
//            for (Cell n : openSet) {
//                if (fScore.get(n) < fScore.get(current)) {
//                    current = n;
//                }
//            }
//
//            if(current.equals(end)){
//                return true;
//            }
//            openSet.remove(current);
//            closedSet.add(current);
//
//            if (pathLength(cameFrom, end) > length)
//                return false;
//
//            for(Cell cell: getConnections.apply(current)){
//                if(closedSet.contains(cell)) continue;
//                double score = gScore.get(current) + current.distanceTo(cell);
//                if(!openSet.contains(cell)) openSet.add(cell);
//                else if(score >= gScore.get(cell)) continue;
//
//                cameFrom.put(cell, current);
//                gScore.put(cell, score);
//                fScore.put(cell, score + cell.distanceTo(end));
//            }
//        }
//        return false;
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
