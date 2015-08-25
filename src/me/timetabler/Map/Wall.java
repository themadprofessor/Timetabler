package me.timetabler.Map;

/**
 * A cell representing a non-traversable area on the map
 */
public class Wall implements CellType {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTraversable() {
        return false;
    }
}
