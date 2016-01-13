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

    /**
     * A map containing all the distances between all other important cells in the map this classroom is in.
     */
    private HashMap<ImportantCell, Integer> distances;

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

    /**
     * If the obj is a ClassRoom, then it will return true if the room numbers are the same. If the obj is not a ClassRoom, then uses the super's equals method.
     * @param obj The object to compare with.
     * @return If the obj is a ClassRoom, then it will return true if the room numbers are the same. If the obj is not a ClassRoom, then uses the super's equals method.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassRoom) {
            return number.equals(((ClassRoom) obj).number);
        } else {
            return super.equals(obj);
        }
    }

    /**
     * Returns the room number rather than is hash as its more useful.
     * @return Returns this classroom's room number.
     */
    @Override
    public String toString() {
        return number;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashMap<ImportantCell, Integer> getDistances() {
        return distances;
    }
}
