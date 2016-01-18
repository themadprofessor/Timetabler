package me.timetabler.map;

import me.timetabler.Coordinates;
import me.util.Log;

/**
 * A recursive method of finding the shortest route between two coordinates. Can cause StackOverflowExceptions if the map is very large.
 */
public class Walker {
    /**
     * The map of the school to be walked on.
     */
    private SchoolMap schoolMap;

    /**
     * The final distance between two important cells. It starts as MAX_VALUE as the walk algorithm checks if its path is lower than this final distance.
     */
    private int finalDistance = Integer.MAX_VALUE;

    /**
     * Initialises the walker.
     * @param schoolMap The map to be traversed.
     */
    public Walker(SchoolMap schoolMap) {
        this.schoolMap = schoolMap;
    }

    /**
     * Finds the shortest distance between the two coordinates on the map give at init.
     * @param start The starting coordinate.
     * @param dest The destination coordinate.
     * @return Returns the shortest distance between the two coordinates.
     */
    public int walk(Coordinates start, Coordinates dest) {
        move(start, dest, start, 0);
        return finalDistance;
    }

    /**
     * Sets the map to be used for traversal. This allows the same walker instance to be used on multiple maps.
     * @param schoolMap The new map the be used for traversal.
     * @return This walker object.
     */
    public Walker setMap(SchoolMap schoolMap) {
        this.schoolMap = schoolMap;
        return this;
    }

    /**
     * A recursive method which determines if the destination cell is adjacent to the current cell, where it checks if finalDistance is higher than the current distance,
     * and sets finalDistance if finalDistance is larger than distance, then winds up the stack to find another route. If the destination cell is not adjacent to the
     * current cell, then moves to the next adjacent traversable cell, checking north, south, east and west in that order, not moving to the cell if it was the last cell.
     * Once walker begins to wind up the stack, it will stop and try an alternative route at every point available, check if it is a shorter rout or even a possible route.
     * @param start The coordinate the walker is currently at.
     * @param dest The final coordinate the walker is aiming to reach.
     * @param last The coordinate the walker was at last.
     * @param distance The current distance along the current path.
     */
    private void move(Coordinates start, Coordinates dest, Coordinates last, int distance) {
        distance++;
        CellType destination = schoolMap.getCell(dest);
        CellType north, south, east, west;
        north = schoolMap.getCell(start.x, start.y - 1);
        south = schoolMap.getCell(start.x, start.y + 1);
        east = schoolMap.getCell(start.x + 1, start.y);
        west = schoolMap.getCell(start.x - 1, start.y);

        if (north.equals(destination) || south.equals(destination) || east.equals(destination) || west.equals(destination)) {
            if (finalDistance > distance) {
                finalDistance = distance;
                Log.verbose("Found New Shortest Distance [" + finalDistance + ']');
            }
            return;
        }
        if (!last.equals(new Coordinates(start.x, start.y - 1)) && north.isTraversable()) {
            Log.verbose("Current Coordinate [" + start + "] Moving North");
            move(new Coordinates(start.x, start.y - 1), dest, start, distance);
        }
        if (!last.equals(new Coordinates(start.x, start.y + 1)) && south.isTraversable()) {
            Log.verbose("Current Coordinate [" + start + "] Moving South");
            move(new Coordinates(start.x, start.y + 1), dest, start, distance);
        }
        if (!last.equals(new Coordinates(start.x + 1, start.y)) && east.isTraversable()) {
            Log.verbose("Current Coordinate [" + start + "] Moving East");
            move(new Coordinates(start.x + 1, start.y), dest, start, distance);
        }
        if (!last.equals(new Coordinates(start.x - 1, start.y)) && west.isTraversable()) {
            Log.verbose("Current Coordinate [" + start + "] Moving West");
            move(new Coordinates(start.x - 1, start.y), dest, start, distance);
        }
    }
}
