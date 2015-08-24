package me.timetabler;

import java.util.Optional;

/**
 * Created by stuart on 24/08/15.
 */
public class ClassRoom implements CellType {
    public String number;

    public ClassRoom(String number) {
        this.number = number;
    }

    @Override
    public boolean isTraversable() {
        return false;
    }
}
