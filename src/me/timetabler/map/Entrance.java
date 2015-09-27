package me.timetabler.map;

import java.util.HashMap;

/**
 * Created by stuart on 25/08/15.
 */
public class Entrance implements ImportantCell {
    private HashMap<CellType, Integer> distances = new HashMap<>();

    @Override
    public boolean isTraversable() {
        return true;
    }

    @Override
    public HashMap<CellType, Integer> getDistances() {
        return distances;
    }

    @Override
    public String toString() {
        return "Entrance";
    }
}
