package me.timetabler.Tests;

import me.timetabler.Coordinates;
import me.timetabler.Map.*;
import me.util.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * Created by stuart on 24/08/15.
 */
public class MapTests {
    SchoolMap map;

    @Before
    public void init() {
        map = new SchoolMap(new File("assets/map.csv"));
    }

    @Test
    public void testGetCell() {
        assumeTrue(map != null);

        CellType cellType = map.getCell(3, 0);
        assertEquals(cellType, new ClassRoom("L3"));
        Log.out("Found classroom");

        cellType = map.getCell(2, 1);
        assertEquals(cellType, new StairCase(8));
        Log.out("Found staircase");

        cellType = map.getCell(3, 1);
        assertEquals(cellType, new Path());
        Log.out("Found Path");
    }

    @Test
    public void testGetRoomCoordinates() {
        assumeTrue(map != null);

        Optional<Coordinates> coordinates = map.getRoomCoordinates("L3");
        assertTrue(coordinates.isPresent());
        assertTrue(coordinates.get().x == 3 && coordinates.get().y == 0);
        Log.out("Found L3");

        coordinates = map.getRoomCoordinates("43");
        assertFalse(coordinates.isPresent());
        Log.out("Failed to find 43");

        coordinates = map.getRoomCoordinates("14");
        assertTrue(coordinates.isPresent());
        assertTrue(coordinates.get().x == 8 && coordinates.get().y == 8);
        Log.out("Found 14");
    }
}
