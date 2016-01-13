package me.timetabler.map;

/**
 * A cell representing a path which can be used for pathing.
 */
public class Path implements CellType {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTraversable() {
        return true;
    }

    /**
     * If obj is a Path, then return true. If obj is not a path, then use the super's equals method.
     * @param obj The object to compared.
     * @return If obj is a Path, then return true. If obj is not a path, then use the super's equals method.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Path) {
            return true;
        } else {
            return super.equals(obj);
        }
    }

    /**
     * Returns the String "0", as this is string used to represent a path in the csv map format.
     * @return "0";
     */
    @Override
    public String toString() {
        return "0";
    }
}
