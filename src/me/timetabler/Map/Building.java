package me.timetabler.Map;

import me.timetabler.Walker;
import me.util.Log;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by stuart on 25/08/15.
 */
public class Building {
    private ArrayList<ClassRoom> classRooms;

    public Building(SchoolMap schoolMap) {
        Optional<ArrayList<ClassRoom>> optional = schoolMap.getAllClassrooms();
        if (optional.isPresent()) {
            classRooms = optional.get();
        } else {
            classRooms = new ArrayList<>();
        }
        classRooms.forEach(source -> classRooms.forEach(destination -> {
            if (!source.equals(destination) && (!source.distances.containsKey(destination.toString()) || !destination.distances.containsKey(source.toString()))) {
                Walker walker = new Walker(schoolMap);
                int distance = walker.walk(schoolMap.getRoomCoordinates(source.number).get(), schoolMap.getRoomCoordinates(destination.number).get());
                source.distances.put(destination.number, distance);
                destination.distances.put(source.number, distance);
                Log.out("[" + distance + "] Between " + source + " and " + destination);
            }
        }));
    }


}
