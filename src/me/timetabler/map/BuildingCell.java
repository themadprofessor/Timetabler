package me.timetabler.map;

import me.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A type of cell which contains of the important cells within it in its map.<br>
 * The init method must be called after the full map has been populated inorder to avoid StackOverflowExceptions.
 */
public class BuildingCell implements ImportantCell {
    /**
     * The important cells within this building.
     */
    private ArrayList<ImportantCell> important;

    /**
     * The distances between this building and other important cells, but not the important cells within the building.
     */
    private HashMap<ImportantCell, Integer> distances;

    /**
     * The unique name of this building.
     */
    public String name;

    /**
     * Initialises the building but does not calculate the distances between the internal important cells.
     * @param name The unique name of the building.
     */
    public BuildingCell(String name) {
        this.name = name;
        important = new ArrayList<>();
        distances = new HashMap<>();
    }

    /**
     * Finishes the initialisation of the building and calculates the distances between the internal important cells.
     * @param schoolMap The map of this building.
     */
    public void init(SchoolMap schoolMap) {
        Log.debug("Initialising BuildingCell [" + name + "]");
        important = schoolMap.getAllImportantCells();
        Walker walker = new Walker(schoolMap);
        if (important.isEmpty()) {
            throw new IllegalStateException("No Important Cells Found in BuildingCell [" + name +']');
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
     * Gets all the buildings within this building, which will be empty if there are none.
     * @return A list of all the buildings within this building, which can be empty.
     */
    public List<BuildingCell> getSubBuildings() {
        ArrayList<BuildingCell> buildingCells = new ArrayList<>();
        important.parallelStream()
                .filter(importantCell -> importantCell instanceof BuildingCell)
                .forEach(buildingCell -> buildingCells.add((BuildingCell) buildingCell));
        return buildingCells;
    }

    /**
     * Gets all the classrooms within this building, which can be empty if there are none.
     * @return A list of all the classrooms within this building, which can be empty.
     */
    public List<ClassroomCell> getSubClassrooms() {
        ArrayList<ClassroomCell> classroomCells = new ArrayList<>();
        important.parallelStream()
                .filter(importantCell -> importantCell instanceof ClassroomCell)
                .forEach(importantCell -> classroomCells.add((ClassroomCell) importantCell));
        return classroomCells;
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
        return distances;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BuildingCell that = (BuildingCell) o;

        return name.equals(that.name);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return name.hashCode();
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
