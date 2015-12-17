//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package me.timetabler.map;

import me.timetabler.Coordinates;
import me.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class SchoolMap {
    private int width;
    private int height;
    private CellType[][] schoolGrid;

    /**
     * Creates a object which represents the map of a building or school. Any building reference is not created to avoid StackOverflowExceptions.
     * @param mapFile The file while contains the CSV map
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

            schoolGrid = new CellType[width][height];
            //A 2D array is an array of arrays, so each top array must be filled with sub arrays
            for (CellType[] cellTypes : schoolGrid) {
                Arrays.fill(cellTypes, new Wall());
            }
            for (int y = 0; y < lines.size(); y++) {
                String[] cellsStrings = lines.get(y).split(",");
                for (int x = 0; x < cellsStrings.length; x++) {
                    if ("".equals(cellsStrings[x])) {
                        //Do nothing as default value is wall
                    } else if ("0".equals(cellsStrings[x])) {
                        schoolGrid[x][y] = new Path();
                    } else if ("Enter".equals(cellsStrings[x])) {
                        schoolGrid[x][y] = new Entrance();
                    } else if (cellsStrings[x].startsWith("C-")) {
                        schoolGrid[x][y] = new ClassRoom(cellsStrings[x].replace("C-", ""));
                    } else {
                        schoolGrid[x][y] = new Building(cellsStrings[x]);
                    }
                }
            }
        } catch (IOException e) {
            Log.err(e);
        }
    }

    /**
     * Uses the already created Building objects to populate the map.
     * @param buildings All buildings which exist in the whole map.
     */
    public void init(HashMap<String, SchoolMap> buildings) {
        ArrayList<Building> optionalBuildings = getAllBuildings();
        if (!optionalBuildings.isEmpty()) {
            optionalBuildings.forEach(building -> buildings.forEach((name, map) -> {
                if (building.name.equals(name)) {
                    building.init(map);
                }
            }));
        } else {
            throw new IllegalStateException("No Buildings found in map!");
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
            return new Wall();
        }
    }

    /**
     * Gets the cell at the given coordinate.
     * @param coordinates The coordinate to look for.
     * @return The cell at the given coordinate.
     */
    public CellType getCell(Coordinates coordinates) {
        return this.getCell(coordinates.x, coordinates.y);
    }

    /**
     * Gets the coordinate of the given cell in the map. If the cell does not exist in the map, the Optional will be empty.
     * @param cell The cell to look for.
     * @return The coordinate of the cell, if present.
     */
    public Optional<Coordinates> getCoordinates(CellType cell) {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                CellType tmpCell = schoolGrid[x][y];
                if (cell.equals(tmpCell)) {
                    return Optional.of(new Coordinates(x, y));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the coordinate of the classroom with the given number/id. If the room does not exist in the map,  the Optional will be empty.
     * @param number The number/id of the classroom.
     * @return The coordinate of the room, if present.
     */
    public Optional<Coordinates> getRoomCoordinates(String number) {
        for(int y = 0; y < this.height - 1; ++y) {
            for(int x = 0; x < this.width - 1; ++x) {
                CellType cell = schoolGrid[x][y];
                if(cell instanceof ClassRoom && ((ClassRoom)cell).number.equals(number)) {
                    return Optional.of(new Coordinates(x, y));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Gets a list of all classrooms in the map.
     * @return The list of classrooms in the map, which can be empty.
     */
    public ArrayList<ClassRoom> getAllClassrooms() {
        ArrayList<ClassRoom> classrooms = new ArrayList<>();

        for(int y = 0; y < this.height - 1; ++y) {
            for(int x = 0; x < this.width - 1; ++x) {
                CellType cell = schoolGrid[x][y];
                if(cell instanceof ClassRoom) {
                    classrooms.add((ClassRoom) cell);
                }
            }
        }
        return classrooms;
    }

    /**
     * Gets a list containing all the buildings in the map.
     * @return The list of buildings in the map, which can be empty.
     */
    public ArrayList<Building> getAllBuildings() {
        ArrayList<Building> buildings = new ArrayList<>();
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                CellType cell = schoolGrid[x][y];
                if (cell instanceof Building) {
                    buildings.add((Building) cell);
                }
            }
        }
        return buildings;
    }

    /**
     * Gets a list of all the ImportantCells(ClassRoom, Building, Entrance in the map.
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
