package me.timetabler.map;

import javafx.concurrent.Task;
import me.timetabler.data.Building;
import me.timetabler.data.Distance;
import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataConnectionException;
import me.timetabler.data.exceptions.DataExceptionHandler;
import me.timetabler.data.exceptions.DataUpdateException;
import me.util.Log;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * A background process which parses map files into the Java, calculates the distance between the classrooms and store
 * the map data in the database.
 */
public class MapLoader extends Task<Void> {
    /**
     * The configuration map containing configuration data for map loading.
     */
    private Map<String, String> config;

    /**
     * The daoManager which will be used to store the building, classroom and distance data.
     */
    private DaoManager daoManager;

    /**
     * Initialises the MapLoader.
     *
     * @param config     A map containing the keys 'top_map' and 'other_maps'.
     * @param daoManager The DaoManger to be used by the MapLoader to store the map data.
     */
    public MapLoader(Map<String, String> config, DaoManager daoManager) {
        this.config = config;
        this.daoManager = daoManager;
    }

    /**
     * Parses the map files, calculates the distances between all classrooms and adds them to the database.
     *
     * @return Nothing.
     */
    @Override
    protected Void call() {
        //Warning: This method utilises lambdas extensively!

        //Initialise temporary data storage
        Map<String, SchoolMap> buildings = new HashMap<>();
        Set<Building> buildingsDb = new HashSet<>();
        HashMap<String, ClassroomCell> classRoomList = new HashMap<>();
        HashSet<Distance> distances = new HashSet<>(); //Use set as eliminates duplicates

        updateMessage("Loading Top Map.");
        updateProgress(0, 6);
        Log.verbose("Loading Top Map From [" + config.get("top_map") + "] And Other Maps From [" + config.get("other_maps") + ']');

        //Loads the top map file
        File mapFolder = new File(config.get("other_maps"));
        SchoolMap schoolMap = new SchoolMap(new File(config.get("top_map")));
        if (mapFolder.isFile()) {
            throw new InvalidParameterException("Map folder is not a folder!");
        }

        //Loads all other map files
        updateMessage("Loading Other Maps");
        updateProgress(1, 6);
        //Get all the files in mapFolder which is a file, ends with .csv and is not top_map.
        File[] files = mapFolder.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".csv") && !pathname.getPath().endsWith(config.get("top_map")));
        for (File file : files) {
            buildings.put(file.getName().replace(".csv", ""), new SchoolMap(file));
        }

        if (buildings.size() == 0) {
            throw new IllegalStateException("Failed to find any buildings");
        }

        //Initialises buildings
        Log.verbose("Loaded [" + files.length + "] Extra Maps");
        updateMessage("Initialising Buildings. May Take A Long Time.");
        updateProgress(2, 6);
        schoolMap.init(buildings);

        //Adds all the buildings from the maps to the database.
        updateMessage("Adding Buildings To Database.");
        updateProgress(3, 6);
        List<BuildingCell> buildingCells = schoolMap.getAllBuildings();
        buildingCells.forEach(building -> {
            try {
                Log.verbose("Adding building [" + building.name + ']');
                Building buildingDb = new Building();
                buildingDb.buildingName = building.name;
                buildingDb.id = daoManager.getBuildingDao().insert(buildingDb);
                buildingsDb.add(buildingDb);
            } catch (DataUpdateException | DataAccessException e) {
                Log.debug("Exception thrown by [" + building.name + ']');
                DataExceptionHandler.handleJavaFx(e, "BuildingCell", false);
            } catch (DataConnectionException e) {
                Log.debug("Exception thrown by [" + building.name + ']');
                DataExceptionHandler.handleJavaFx(e, "BuildingCell", true);
            }
        });
        Building topBuilding = new Building();
        topBuilding.buildingName = "Top";
        try {
            topBuilding.id = daoManager.getBuildingDao().insert(topBuilding);
            buildingsDb.add(topBuilding);
        } catch (DataAccessException | DataUpdateException e) {
            Log.debug("Exception thrown by [Top]");
            DataExceptionHandler.handleJavaFx(e, "BuildingCell", false);
        } catch (DataConnectionException e) {
            Log.debug("Exception thrown by [Top]");
            DataExceptionHandler.handleJavaFx(e, "BuildingCell", true);
        }

        //Return nothing
        return null;
    }
}