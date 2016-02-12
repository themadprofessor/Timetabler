package me.timetabler.map;

import me.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A type of cell which contains of the important cells within it in its map.<br>
 * The init method must be called after the full map has been populated inorder to avoid StackOverflowExceptions.
 */
public class Building implements ImportantCell {
    /**
     * The important cells within this building.
     */
    private ArrayList<ImportantCell> important;

    /**
     * The unique name of this building.
     */
    public String name;

    /**
     * Initialises the building but does not calculate the distances between the internal important cells.
     * @param name The unique name of the building.
     */
    public Building(String name) {
        this.name = name;
        important = new ArrayList<>();
    }

    /**
     * Finishes the initialisation of the building and calculates the distances between the internal important cells.
     * @param schoolMap The map of this building.
     */
    public void init(SchoolMap schoolMap) {
        important = schoolMap.getAllImportantCells();
        Walker walker = new Walker(schoolMap);
        if (important.isEmpty()) {
            throw new IllegalStateException("No Important Cells Found in Building [" + name +']');
        }
        important.forEach(source -> important.forEach(destination -> {
            if (!source.equals(destination) && (!source.getDistances().containsKey(destination) || !destination.getDistances().containsKey(source))) {
                int distance = walker.walk(schoolMap.getCoordinates(source).get(), schoolMap.getCoordinates(destination).get());
                source.getDistances().put(destination, distance);
                destination.getDistances().put(source, distance);
                Log.debug("[" + distance + "] Between " + source + " and " + destination);
            }
        }));
    }

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
        HashMap<ImportantCell, Integer> distances = new HashMap<>();
        important.forEach(cell -> {
            Log.verbose(cell + " Distances Found By " + name);
            distances.putAll(cell.getDistances());
        });
        return distances;
    }

    /**
     * Returns the name of the building rather than its hash as it is more useful.
     * @return Returns the name of the building.
     */
    @Override
    public String toString() {
        return name;
    }
}
