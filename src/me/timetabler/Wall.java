package me.timetabler;

/**
 * Created by stuart on 24/08/15.
 */
public class Wall implements CellType {
    @Override
    public boolean isTraversable() {
        return false;
    }
}
