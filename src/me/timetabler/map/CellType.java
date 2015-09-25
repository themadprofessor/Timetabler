package me.timetabler.map;

/**
 * The type of cell which can appear with the map
 */
public interface CellType {
    /**
     * Is the cell able to be used to find a path
     * @return True if the cell is traversable
     */
    boolean isTraversable();
}
