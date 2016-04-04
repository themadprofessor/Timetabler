package me.timetabler.map;

import javafx.concurrent.Task;
import me.timetabler.data.Building;
import me.timetabler.data.Classroom;
import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataConnectionException;
import me.timetabler.data.exceptions.DataExceptionHandler;
import me.timetabler.data.exceptions.DataUpdateException;
import me.util.Log;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A background process which parses map files into the Java, calculates the distance between the classrooms and store
 * the map data in the database.
 */
public class MapLoader extends Task<Void> {
    private Map<String, String> config;
    private DaoManager daoManager;

    /**
     * Initialises the MapLoader.
     * @param config A map containing the keys 'top_map' and 'other_maps'.
     * @param daoManager The DaoManger to be used by the MapLoader to store the map data.
     */
    public MapLoader(Map<String, String> config, DaoManager daoManager) {
        this.config = config;
        this.daoManager = daoManager;
    }

    /**
     * Parses the map files, calculates the distances between all classrooms and adds them to the database.
     * @return Nothing.
     */
    @Override
    protected Void call() {
        updateMessage("Loading Top Map.");
        updateProgress(0, 6);
        Log.verbose("Loading Top Map From [" + config.get("top_map") + "] And Other Maps From [" +  config.get("other_maps") + ']');

        File mapFolder = new File(config.get("other_maps"));
        SchoolMap schoolMap = new SchoolMap(new File(config.get("top_map")));
        if (mapFolder.isFile()) {
            throw new InvalidParameterException("Map folder is not a folder!");
        }

        updateMessage("Loading Other Maps");
        updateProgress(1, 6);
        File[] files = mapFolder.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".csv") && !pathname.getPath().endsWith(config.get("top_map")));
        Map<String, SchoolMap> buildings = new HashMap<>();
        for (File file : files) {
            buildings.put(file.getName().replace(".csv", ""), new SchoolMap(file));
        }

        if (buildings.size() == 0) {
            throw new IllegalStateException("Failed to find any buildings");
        }
        Log.verbose("Loaded [" + files.length + "] Extra Maps");
        updateMessage("Initialising Buildings. May Take A Long Time.");
        updateProgress(2, 6);
        schoolMap.init(buildings);

        updateMessage("Adding Buildings To Database.");
        updateProgress(3, 6);
        List<me.timetabler.map.Building> buildingCells = schoolMap.getAllBuildings();
        List<Building> buildingsDb = new ArrayList<>();
        buildingCells.forEach(building -> {
            try {
                Building buildingDb = new Building();
                buildingDb.buildingName = building.name;
                buildingDb.id = daoManager.getBuildingDao().insert(buildingDb);
                buildingsDb.add(buildingDb);
            } catch (DataUpdateException | DataAccessException e) {
                Log.error(e);
                DataExceptionHandler.handleJavaFx(e, "Building", false);
            } catch (DataConnectionException e) {
                Log.error(e);
                DataExceptionHandler.handleJavaFx(e, "Building", true);
            }
        });
        Building top = new Building();
        top.buildingName = "Top";
        try {
            top.id = daoManager.getBuildingDao().insert(new Building(-1, "Top"));
        } catch (DataUpdateException | DataAccessException e) {
            DataExceptionHandler.handleJavaFx(e, "building", false);
        } catch (DataConnectionException e) {
            DataExceptionHandler.handleJavaFx(e, "building", true);
        }
        buildingsDb.add(top);

        updateMessage("Adding Classrooms To Database.");
        updateProgress(4, 6);
        ArrayList<Classroom> classrooms = new ArrayList<>();
        schoolMap.getAllClassrooms().forEach(classRoom -> {
            Classroom room = new Classroom();
            room.name = classRoom.number;
            room.building = top;
            try {
                room.subject = daoManager.getSubjectDao().getByName(classRoom.subject).orElseThrow(DataAccessException::new);
                room.id = daoManager.getClassroomDao().insert(room);
                classrooms.add(room);
            } catch (DataUpdateException | DataAccessException e) {
                Log.error(e);
                DataExceptionHandler.handleJavaFx(e, "Building", false);
            } catch (DataConnectionException e) {
                Log.error(e);
                DataExceptionHandler.handleJavaFx(e, "Building", true);
            }
        });

        buildingsDb.forEach(building -> buildings.forEach((name, map) -> {
            if (name.equals(building.buildingName)) {
                map.getAllClassrooms().forEach(classRoom -> {
                    Log.verbose("Adding classroom [" + classRoom.number + ',' + classRoom.subject + ']');
                    Classroom room = new Classroom();
                    room.name = classRoom.number;
                    room.building = building;
                    try {
                        room.subject = daoManager.getSubjectDao().getByName(classRoom.subject).orElseThrow(DataAccessException::new);
                        room.id = daoManager.getClassroomDao().insert(room);
                        classrooms.add(room);
                    } catch (DataUpdateException | DataAccessException e) {
                        Log.verbose("Error thrown by [" + classRoom.number + ',' + classRoom.subject + ']');
                        Log.error(e);
                        DataExceptionHandler.handleJavaFx(e, "Building", false);
                    } catch (DataConnectionException e) {
                        Log.error(e);
                        DataExceptionHandler.handleJavaFx(e, "Building", true);
                    }
                });
            }
        }));

        return null;
    }
}
