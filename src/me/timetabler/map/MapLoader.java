package me.timetabler.map;

import javafx.concurrent.Task;
import me.timetabler.data.Building;
import me.timetabler.data.Classroom;
import me.timetabler.data.Distance;
import me.timetabler.data.Subject;
import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataConnectionException;
import me.timetabler.data.exceptions.DataExceptionHandler;
import me.timetabler.data.exceptions.DataUpdateException;
import me.util.Log;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

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
        try {
            //Initialise temporary data storage
            Map<String, SchoolMap> buildings = new HashMap<>();
            Map<String, Building> buildingsDb = new HashMap<>();
            Map<String, Classroom> classroomDb = new HashMap<>();
            Set<Distance> distances = new HashSet<>();

            //Check if the task has been cancelled, and stop if it has.
            if (isCancelled()) {
                updateMessage("Cancelled");
                updateProgress(-1, 5);
                return null;
            }

            updateMessage("Loading Top Map.");
            updateProgress(0, 6);
            Log.verbose("Loading Top Map From [" + config.get("top_map") + "] And Other Maps From [" + config.get("other_maps") + ']');

            //Loads the top map file
            File mapFolder = new File(config.get("other_maps"));
            SchoolMap schoolMap = new SchoolMap(new File(config.get("top_map")));
            if (mapFolder.isFile()) {
                throw new InvalidParameterException("Map folder is not a folder!");
            }

            //Check if the task has been cancelled, and stop if it has.
            if (isCancelled()) {
                updateMessage("Cancelled");
                updateProgress(-1, 5);
                return null;
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

            //Check if the task has been cancelled, and stop if it has.
            if (isCancelled()) {
                updateMessage("Cancelled");
                updateProgress(-1, 5);
                return null;
            }

            //Initialises buildings
            Log.verbose("Loaded [" + files.length + "] Extra Maps");
            updateMessage("Initialising Buildings. May Take A Long Time.");
            updateProgress(2, 6);
            buildings.put("Top", schoolMap);
            schoolMap.init(buildings);

            //Check if the task has been cancelled, and stop if it has.
            if (isCancelled()) {
                updateMessage("Cancelled");
                updateProgress(-1, 5);
                return null;
            }

            //Adds all the buildings from the maps to the database.
            updateMessage("Adding Buildings To Database.");
            updateProgress(3, 6);
            Map<String, BuildingCell> buildingCells = new HashMap<>();
            schoolMap.getAllBuildings().forEach(buildingCell -> buildingCells.put(buildingCell.name, buildingCell));
            int changeCount;

            do {
                changeCount = 0;
                Map<String, BuildingCell> tmpMap = new HashMap<>();
                Collection<BuildingCell> buildingsToSearch = buildingCells.values();
                for (BuildingCell buildingCell : buildingsToSearch) {
                    List<BuildingCell> subBuildings = buildingCell.getSubBuildings();
                    for (BuildingCell subBuilding : subBuildings) {
                        if (subBuilding == null) {
                            Log.error("cry");
                        }
                        if (!buildingCells.containsKey(subBuilding.name)) {
                            Log.verbose("Found building [" + subBuilding.name + ']');
                            tmpMap.put(subBuilding.name, subBuilding);
                            changeCount++;
                        }
                    }
                }
                buildingCells.putAll(tmpMap);
            } while (changeCount > 0);
            buildingCells.forEach((name, building) -> addBuildingToDb(building, buildingsDb));


            //Check if the task has been cancelled, and stop if it has.
            if (isCancelled()) {
                updateMessage("Cancelled");
                updateProgress(-1, 5);
                return null;
            }

            //Create a building for classrooms at the top level
            Building topBuilding = new Building();
            topBuilding.buildingName = "Top";
            try {
                topBuilding.id = daoManager.getBuildingDao().insert(topBuilding);
                buildingsDb.put(topBuilding.buildingName, topBuilding);
            } catch (DataAccessException | DataUpdateException e) {
                updateMessage("Failed to add building [Top] because of [" + e.getLocalizedMessage() + ']');
                updateProgress(-1, 5);
                return null;
            } catch (DataConnectionException e) {
                Log.error("Lost connection to data source when adding building [Top]");
                DataExceptionHandler.handleJavaFx(e, "BuildingCell", true);
            }

            //Check if the task has been cancelled, and stop if it has.
            if (isCancelled()) {
                updateMessage("Cancelled");
                updateProgress(-1, 5);
                return null;
            }

            updateMessage("Adding classrooms");
            updateProgress(4, 6);

            List<ClassroomCell> classroomCells = new ArrayList<>();
            String failedLog = addClassroomsToDb(schoolMap.getAllClassrooms(), topBuilding, classroomDb);
            classroomCells.addAll(schoolMap.getAllClassrooms());

            //Stop and display the errors if it failed to load any classrooms.
            if (failedLog.length() > 0) {
                updateMessage("ERROR!\n" + failedLog);
                updateProgress(-1, 5);
                return null;
            }

            //Check if the task has been cancelled, and stop if it has.
            if (isCancelled()) {
                updateMessage("Cancelled");
                updateProgress(-1, 5);
                return null;
            }

            StringBuilder failedBuilder = new StringBuilder();
            buildingCells.values().forEach(buildingCell -> {
                List<ClassroomCell> classrooms = new ArrayList<>();
                buildingCell.getSubClassrooms().forEach(classrooms::add);
                failedBuilder.append(addClassroomsToDb(classrooms, buildingsDb.get(buildingCell.name), classroomDb));
                classroomCells.addAll(classrooms);
            });

            //Stop and display the errors if it failed to load any classrooms.
            if (failedBuilder.length() > 0) {
                updateMessage("ERROR!\n" + failedBuilder.toString());
                updateProgress(-1, 5);
                return null;
            }

            //Check if the task has been cancelled, and stop if it has.
            if (isCancelled()) {
                updateMessage("Cancelled");
                updateProgress(-1, 5);
                return null;
            }

            updateMessage("Calculating distances");
            updateProgress(5, 6);

            failedLog = addDistancesToDb(classroomDb, classroomCells);

            if (failedLog.length() > 0) {
                updateMessage("ERROR! \n" + failedLog);
                updateProgress(-1, 6);
                return null;
            }

            updateMessage("Done");
            updateProgress(6, 6);
            //Return nothing
            return null;
        } catch (Exception e) {
            Log.error(e);
            updateMessage("Uncaught exception\n" + e.getLocalizedMessage());
            updateProgress(-1, 6);
            return null;
        }
    }

    /**
     * Adds all the given classrooms to the data source with the given building and stores the classroom in the given
     * map. The classrooms are assumed to be within the given building. The method returns an error log string which
     * contains error information for the user, but will be empty if there were no errors.
     * @param classrooms The classrooms to be added.
     * @param building The building the classrooms are in.
     * @param classroomsDb The map of classrooms to also store the classrooms in.
     * @return An error string for the user, which can be empty.
     */
    private String addClassroomsToDb(List<ClassroomCell> classrooms, Building building, Map<String, Classroom> classroomsDb) {
        StringBuilder failedBuilder = new StringBuilder();
        classrooms.remove(null);

        //Load the classrooms for the top map
        classrooms.stream().sequential().forEach(classroomCell -> {
            try {
                //Check the subject specified for the classroom exists, if it does not, log it and return.
                Optional<Subject> subjectOptional = daoManager.getSubjectDao().getByName(classroomCell.subject);
                if (!subjectOptional.isPresent()) {
                    failedBuilder.append("Failed to load classroom [")
                            .append(classroomCell.number)
                            .append("] because its subject [")
                            .append(classroomCell.subject)
                            .append("] is not in the system\n\n");
                    return;
                }

                Classroom classroom = new Classroom();
                classroom.building = building;
                classroom.subject = subjectOptional.get();
                classroom.name = classroomCell.number;
                classroom.id = daoManager.getClassroomDao().insert(classroom);
                classroomsDb.put(classroom.name, classroom);
            } catch (DataAccessException | DataUpdateException e) {
                Log.error(e);
                failedBuilder.append("Failed to add classroom [")
                        .append(classroomCell.number)
                        .append("] to data source, because of [")
                        .append(e.getLocalizedMessage())
                        .append(']');
            } catch (DataConnectionException e) {
                Log.error("Lost connection to database when loading classroom [" + classroomCell.number + ']');
                DataExceptionHandler.handleJavaFx(e, "classroom", true);
            }
        });

        return failedBuilder.toString();
    }

    /**
     * Adds the given building to the data source and stores it in the given list. The method returns an error log
     * string which contains error information for the user, but will be empty if there were no errors.
     * @param buildingCell The building to be added to the data source.
     * @param buildingsDb The map to also store the building in.
     * @return An error string for the user, which can be empty.
     */
    private String addBuildingToDb(BuildingCell buildingCell, Map<String, Building> buildingsDb) {
        StringBuilder failedBuilder = new StringBuilder();

        try {
            Log.verbose("Adding building [" + buildingCell.name + ']');
            Building buildingDb = new Building();
            buildingDb.buildingName = buildingCell.name;
            buildingDb.id = daoManager.getBuildingDao().insert(buildingDb);
            buildingsDb.put(buildingDb.buildingName, buildingDb);
        } catch (DataUpdateException | DataAccessException e) {
            Log.error(e);
            failedBuilder.append("Failed to add building [")
                    .append(buildingCell.name)
                    .append("] to data source, because of [").
                    append(e.getLocalizedMessage())
                    .append(']');
        } catch (DataConnectionException e) {
            Log.error("Lost connection to database when loading building [" + buildingCell.name + ']');
            DataExceptionHandler.handleJavaFx(e, "building", true);
        }

        return failedBuilder.toString();
    }

    /**
     * Pulls all the distances between the classrooms from each building, within the same building. Then, adds the
     * found distances to the database. The method returns an error log string which contains error information, which
     * should be displayed to the user, but will be empty if there were no errors.
     * @param classrooms The classroom objects already in the data source.
     * @param classroomCells The classrooms in the map.
     * @return An error string for the user, which can be empty.
     */
    private String addDistancesToDb(Map<String, Classroom> classrooms, List<ClassroomCell> classroomCells) {
        List<Distance> distances = new ArrayList<>();

        //Add all the distances from the classrooms in the same building as each other
        classroomCells.forEach(classroomCell -> classroomCell.getDistances().forEach(((importantCell, dist) -> {
            if (importantCell instanceof ClassroomCell) {
                //Create a new distance object for the distance.
                Distance distance = new Distance();
                distance.startRoom = classrooms.get(classroomCell.number);
                distance.endRoom = classrooms.get(((ClassroomCell) importantCell).number);
                distance.distance = dist;

                //Find all the same distances and add the new one to the found list
                List<Distance> sameTrips = distances.parallelStream().filter(distance1 ->
                        (distance1.startRoom.equals(distance.startRoom) && distance1.endRoom.equals(distance.endRoom))
                                ||
                                (distance1.startRoom.equals(distance.endRoom) && distance1.endRoom.equals(distance.startRoom)))
                        .collect(Collectors.toList());
                sameTrips.add(distance);

                //Sort the list in order of the distance, remove all the same trips from the full list to avoid
                //duplicates, add the first distance in the sorted list back as it will be the shortest.
                sameTrips.sort((o1, o2) -> o1.distance - o2.distance);
                distances.removeAll(sameTrips);
                if (sameTrips.size() > 1) {
                    distances.add(sameTrips.get(0));
                    Log.verbose("Adding shortest distance for a trip [" + sameTrips.get(0) + ']');
                } else {
                    distances.add(sameTrips.get(0));
                    Log.verbose("Adding distance for new trip [" + sameTrips.get(0) + ']');
                }
            }
        })));

        StringBuilder failedBuilder = new StringBuilder();

        distances.stream().sequential().forEach(distance -> {
            try {
                daoManager.getDistanceDao().insert(distance);
            } catch (DataAccessException | DataUpdateException e) {
                Log.error(e);
                failedBuilder.append("Failed to add distance [")
                        .append(distance.toString())
                        .append("] to data source, because [")
                        .append(e.getLocalizedMessage())
                        .append(']');
            } catch (DataConnectionException e) {
                DataExceptionHandler.handleJavaFx(e, "distance", true);
            }
        });

        return failedBuilder.toString();
    }
}