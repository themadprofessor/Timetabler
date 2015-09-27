package me.timetabler.map;

import java.util.HashMap;

/**
 * C cell which contains information about distances to other cells
 */
public interface ImportantCell extends CellType {
    HashMap<CellType, Integer> getDistances();
}
