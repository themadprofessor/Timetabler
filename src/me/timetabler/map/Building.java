package me.timetabler.map;

import me.timetabler.Walker;
import me.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by stuart on 25/08/15.
 */
public class Building implements ImportantCell {
    private ArrayList<ImportantCell> important;
    public String name;

    public Building(String name) {
        this.name = name;
        important = new ArrayList<>();
    }

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

    @Override
    public boolean isTraversable() {
        return true;
    }

    @Override
    public HashMap<CellType, Integer> getDistances() {
        HashMap<CellType, Integer> distances = new HashMap<>();
        important.forEach(cell -> {
            Log.out(cell);
            distances.putAll(cell.getDistances());
        });
        return distances;
    }

    @Override
    public String toString() {
        return name;
    }
}
