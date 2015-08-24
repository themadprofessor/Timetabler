package me.timetabler;

/**
 * Created by stuart on 24/08/15.
 */
public class StairCase implements CellType {
    public int number;

    public StairCase(int number) {
        this.number = number;
    }

    @Override
    public boolean isTraversable() {
        return true;
    }
}
