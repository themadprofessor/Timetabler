package me.timetabler.map;

import java.util.HashMap;

/**
 * A cell which represents a class room containing it's room number
 */
public class ClassRoom implements ImportantCell {
    /**
     * The number the room is identified with
     */
    public String number;

    private HashMap<CellType, Integer> distances;

    /**
     * Creates a cell which represents a class room
     * @param number The room's identifier
     */
    public ClassRoom(String number) {
        this.number = number;
        distances = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTraversable() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassRoom) {
            return number.equals(((ClassRoom) obj).number);
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public String toString() {
        return number;
    }

    @Override
    public HashMap<CellType, Integer> getDistances() {
        return distances;
    }
}
