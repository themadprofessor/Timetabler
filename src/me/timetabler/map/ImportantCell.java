package me.timetabler.map;

import java.util.HashMap;

/**
 * C cell which contains information about distances to other cells
 */
public interface ImportantCell extends CellType {
    /**
     * Returns the distances between this cell and other cells.
     * @return A hash map contain the distances between cells and this cell.
     */
    HashMap<CellType, Integer> getDistances();
}
