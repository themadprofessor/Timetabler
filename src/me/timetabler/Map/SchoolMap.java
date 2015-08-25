//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package me.timetabler.Map;

import me.timetabler.Coordinates;
import me.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

public class SchoolMap {
    private int width;
    private int height;
    private CellType[][] schoolGrid;

    public SchoolMap(File mapFile) {
        Pattern stairRegex = Pattern.compile("-[0-9]+");

        try {

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
                    } else if (stairRegex.matcher(cellsStrings[x]).matches()) {
                        schoolGrid[x][y] = new StairCase(Math.abs(Integer.valueOf(cellsStrings[x])));
                    } else {
                        schoolGrid[x][y] = new ClassRoom(cellsStrings[x]);
                    }
                }
            }
            /*Files.lines(mapFile.toPath()).forEachOrdered((line) -> {
                String[] rowStrings = line.split(",");
                ArrayList row = new ArrayList();
                String[] var5 = rowStrings;
                int var6 = rowStrings.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    String rowString = var5[var7];
                    if("".equals(rowString)) {
                        row.add(new Wall());
                    } else if("0".equals(rowString)) {
                        row.add(new Path());
                    } else if(stairRegex.matcher(rowString).matches()) {
                        row.add(new StairCase(Integer.valueOf(rowString).intValue() * -1));
                    } else {
                        row.add(new ClassRoom(rowString));
                    }
                }

                this.mapList.add(row);
                if(this.width < row.size()) {
                    this.width = row.size();
                }

                this.height = this.mapList.size();
            });*/
        } catch (IOException e) {
            Log.err(e);
        }

    }

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

    public CellType getCell(Coordinates coordinates) {
        return this.getCell(coordinates.x, coordinates.y);
    }

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

    public Optional<ArrayList<StairCase>> getStaircases(int number) {
        ArrayList<StairCase> stairs = new ArrayList<>();

        for(int x = 0; x < this.width; ++x) {
            for(int y = 0; y < this.height; ++y) {
                CellType cell = schoolGrid[x][y];
                if(cell instanceof StairCase && ((StairCase)cell).number == number) {
                    stairs.add((StairCase) cell);
                }
            }
        }

        if(stairs.size() != 0) {
            return Optional.of(stairs);
        } else {
            return Optional.empty();
        }
    }

    public Optional<ArrayList<ClassRoom>> getAllClassrooms() {
        ArrayList<ClassRoom> classrooms = new ArrayList<>();

        for(int y = 0; y < this.height - 1; ++y) {
            for(int x = 0; x < this.width - 1; ++x) {
                CellType cell = schoolGrid[x][y];
                if(cell instanceof ClassRoom) {
                    classrooms.add((ClassRoom) cell);
                }
            }
        }

        if(classrooms.size() != 0) {
            return Optional.of(classrooms);
        } else {
            return Optional.empty();
        }
    }

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
