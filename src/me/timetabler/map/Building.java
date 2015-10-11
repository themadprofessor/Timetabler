package me.timetabler.map;

import me.timetabler.Walker;
import me.util.Log;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by stuart on 25/08/15.
 */
public class Building implements ImportantCell {
    private ArrayList<ImportantCell> important;
    public String name;

    public Building(String name) {
        this.name = name;
    }

    public void init(SchoolMap schoolMap) {
        Optional<ArrayList<ImportantCell>> optional = schoolMap.getAllImportantCells();
        if (optional.isPresent()) {
            important = optional.get();
        } else {
            important = new ArrayList<>();
        }
        Walker walker = new Walker(schoolMap);
        Optional<ArrayList<ImportantCell>> important = schoolMap.getAllImportantCells();
        if (!important.isPresent()) {
            throw new InvalidStateException("No Important Cells Found in Building [" + name +']');
        }
        important.get().forEach(source -> important.get().forEach(destination -> {
            if (!source.equals(destination) && (!source.getDistances().containsKey(destination) || !destination.getDistances().containsKey(source))) {
                int distance = walker.walk(schoolMap.getCoordinates(source).get(), schoolMap.getCoordinates(destination).get());
                source.getDistances().put(destination, distance);
                destination.getDistances().put(source, distance);
                Log.out("[" + distance + "] Between " + source + " and " + destination);
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
        important.forEach(classRoom -> distances.putAll(classRoom.getDistances()));
        return distances;
    }

    @Override
    public String toString() {
        return name;
    }
}
