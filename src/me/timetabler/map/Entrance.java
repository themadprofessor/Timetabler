package me.timetabler.map;

import java.util.HashMap;

/**
 * An entrance to a building from within the building. Used to mark places where a walker would be when it enters a building.
 */
public class Entrance implements ImportantCell {
    /**
     * A map of the distances between this cell and all other important cells in the map this cell is in.
     */
    private HashMap<ImportantCell, Integer> distances = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTraversable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashMap<ImportantCell, Integer> getDistances() {
        return distances;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Entrance";
    }
}
