//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package me.timetabler.map;

import me.timetabler.Coordinate;
import me.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * A representation of the map of the school with utility methods to find information about the layout of the school.
 */
public class SchoolMap {
    /**
     * The width of the map.
     */
    private int width;

    /**
     * The height of the map.
     */
    private int height;

    /**
     * The map of the school represented in an easy to populate and use form.
     */
    private CellType[][] schoolGrid;

    /**
     * Creates a object which represents the map of a building or school. Any building reference is not created to avoid StackOverflowExceptions.
     * @param mapFile The file while contains the CSV map.
     */
    public SchoolMap(File mapFile) {
        try {
            if (!mapFile.exists()) {
                throw new FileNotFoundException(mapFile.getName());
            }
            ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(mapFile.toPath());
            height = lines.size();
            lines.forEach(line -> {
                if (line.length() > width) {
                    width = line.length();
                }
            });
            Log.debug("Loaded Map Of Dimensions [" + width + "," + height +']');

            schoolGrid = new CellType[width][height];
            //A 2D array is an array of arrays, so each top array must be filled with sub arrays
            for (CellType[] cellTypes : schoolGrid) {
                Arrays.fill(cellTypes, new Wall());
            }
            for (int y = 0; y < lines.size(); y++) {
                String[] cellsStrings = lines.get(y).split(",");
                Log.verbose("Found [" + cellsStrings.length + "] Cells");
                for (int x = 0; x < cellsStrings.length; x++) {
                    if ("".equals(cellsStrings[x])) {
                        //Do nothing as default value is wall
                    } else if ("0".equals(cellsStrings[x])) {
                        schoolGrid[x][y] = new Path();
                    } else if (cellsStrings[x].startsWith("C-")) {
                        String[] split = cellsStrings[x].replace("C-", "").split("-");
                        schoolGrid[x][y] = new ClassroomCell(split[0], split[1]);
                    } else {
                        schoolGrid[x][y] = new BuildingCell(cellsStrings[x]);
                    }
                }
            }
        } catch (IOException e) {
            Log.error(e);
        }
    }

    /**
     * Uses the already created BuildingCell objects to populate the map.
     * @param buildings All buildings which exist in the whole map.
     */
    public void init(Map<String, SchoolMap> buildings) {
        ArrayList<BuildingCell> optionalBuildingCells = getAllBuildings();
        Log.verbose("Found [" + optionalBuildingCells.size() + "] Buildings");
        if (!optionalBuildingCells.isEmpty()) {
            optionalBuildingCells.forEach(building -> buildings.forEach((name, map) -> {
                if (building.name.equals(name)) {
                    building.init(map);
                    building.getSubBuildings().forEach(buildingCell -> buildingCell.init(buildings.get(buildingCell.name)));
                }
            }));
            Walker walker = new Walker(this);
            ArrayList<ImportantCell> importantCells = getAllImportantCells();
            importantCells.forEach(startBuilding -> importantCells.forEach(endBuilding -> {
                if (!startBuilding.equals(endBuilding)
                        && !startBuilding.getDistances().containsKey(endBuilding)
                        || !endBuilding.getDistances().containsKey(startBuilding)) {
                    int distance = walker.walk(getCoordinates(startBuilding).get(), getCoordinates(endBuilding).get());
                    startBuilding.getDistances().put(endBuilding, distance);
                    endBuilding.getDistances().put(startBuilding, distance);
                    Log.debug("Distance [" + distance + "] between " + startBuilding + " and " + endBuilding);
                }
            }));
        } else {
            Log.warning("No Buildings Found!");
        }
    }

    /**
     * Gets the Cell at the given coordinate.
     * @param x The x component of the coordinate.
     * @param y The y component of the coordinate
     * @return The cell at the given coordinate.
     */
    public CellType getCell(int x, int y) {
        if(x != this.width && y != this.height && x >= 0 && y >= 0) {
            Object cell = schoolGrid[x][y];
            if(cell == null) {
                cell = new Wall();
            }

            return (CellType)cell;
        } else {
            //Removes the need for ArrayOutOfBoundsExceptions and NullPointerExceptions
            return new Wall();
        }
    }

    /**
     * Gets the cell at the given coordinate.
     * @param coordinate The coordinate to look for.
     * @return The cell at the given coordinate.
     */
    public CellType getCell(Coordinate coordinate) {
        return this.getCell(coordinate.x, coordinate.y);
    }

    /**
     * Gets the coordinate of the given cell in the map. If the cell does not exist in the map, the Optional will be empty.
     * @param cell The cell to look for.
     * @return The coordinate of the cell, if present.
     */
    public Optional<Coordinate> getCoordinates(CellType cell) {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                CellType tmpCell = schoolGrid[x][y];
                if (cell.equals(tmpCell)) {
                    return Optional.of(new Coordinate(x, y));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the coordinate of the classroom with the given number/name. If the room does not exist in the map,  the Optional will be empty.
     * @param number The number/name of the classroom.
     * @return The coordinate of the room, if present.
     */
    public Optional<Coordinate> getRoomCoordinates(String number) {
        for(int y = 0; y < this.height - 1; ++y) {
            for(int x = 0; x < this.width - 1; ++x) {
                CellType cell = schoolGrid[x][y];
                if(cell instanceof ClassroomCell && ((ClassroomCell)cell).number.equals(number)) {
                    return Optional.of(new Coordinate(x, y));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Gets a list of all classrooms.csv in the map.
     * @return The list of classrooms.csv in the map, which can be empty.
     */
    public ArrayList<ClassroomCell> getAllClassrooms() {
        ArrayList<ClassroomCell> classrooms = new ArrayList<>();

        for(int y = 0; y < this.height - 1; ++y) {
            for(int x = 0; x < this.width - 1; ++x) {
                CellType cell = schoolGrid[x][y];
                if(cell instanceof ClassroomCell) {
                    classrooms.add((ClassroomCell) cell);
                }
            }
        }
        return classrooms;
    }

    /**
     * Gets a list containing all the buildings in the map.
     * @return The list of buildings in the map, which can be empty.
     */
    public ArrayList<BuildingCell> getAllBuildings() {
        ArrayList<BuildingCell> buildingCells = new ArrayList<>();
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                CellType cell = schoolGrid[x][y];
                if (cell instanceof BuildingCell) {
                    buildingCells.add((BuildingCell) cell);
                }
            }
        }
        return buildingCells;
    }

    /**
     * Gets a list of all the ImportantCells(ClassroomCell, BuildingCell, Entrance) in the map.
     * @return The list of the ImportantCells in the map which can be empty.
     */
    public ArrayList<ImportantCell> getAllImportantCells() {
        ArrayList<ImportantCell> cells = new ArrayList<>();
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                CellType cell = schoolGrid[x][y];
                if (cell instanceof ImportantCell) {
                    cells.add((ImportantCell) cell);
                }
            }
        }
        return cells;
    }

    /**
     * Creates a string representation of the map as a CSV.
     * @return The string representation of the map.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width - 1; x++) {
                if (schoolGrid[x][y] instanceof Wall) {
                    builder.append(',');
                } else {
                    builder.append(schoolGrid[x][y]).append(',');
                }
            }
            if (schoolGrid[width - 1][y] instanceof Wall) {
                builder.append('\n');
            } else {
                builder.append(schoolGrid[width - 1][y]).append('\n');
            }
        }

        return builder.toString();
    }
}
