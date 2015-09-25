package me.timetabler.map;

/**
 * A cell representing a path which can be used for pathing
 */
public class Path implements CellType {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTraversable() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Path) {
            return true;
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public String toString() {
        return "0";
    }
}
