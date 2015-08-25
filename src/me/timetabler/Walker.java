package me.timetabler;

import me.timetabler.Map.CellType;
import me.timetabler.Map.SchoolMap;

/**
 * Created by stuart on 25/08/15.
 */
public class Walker {
    private SchoolMap schoolMap;
    private int finalDistance = Integer.MAX_VALUE;

    public Walker(SchoolMap schoolMap) {
        this.schoolMap = schoolMap;
    }

    public int walk(Coordinates start, Coordinates dest) {
        move(start, dest, start, 0);
        return finalDistance;
    }

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
            }
            return;
        }
        if (!last.equals(new Coordinates(start.x, start.y - 1)) && north.isTraversable()) {
            move(new Coordinates(start.x, start.y - 1), dest, start, distance);
        }
        if (!last.equals(new Coordinates(start.x, start.y + 1)) && south.isTraversable()) {
            move(new Coordinates(start.x, start.y + 1), dest, start, distance);
        }
        if (!last.equals(new Coordinates(start.x + 1, start.y)) && east.isTraversable()) {
            move(new Coordinates(start.x + 1, start.y), dest, start, distance);
        }
        if (!last.equals(new Coordinates(start.x - 1, start.y)) && west.isTraversable()) {
            move(new Coordinates(start.x - 1, start.y), dest, start, distance);
        }

        return;
    }
}
