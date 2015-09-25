package me.timetabler.map;

/**
 * A cell which represents a staircase which is used to traverse floors to its corresponding staircase
 */
public class StairCase implements CellType {
    /**
     * The number of this staircase
     */
    public int number;

    /**
     * Creates a staircase with the given number, which there must be at least two of
     * @param number
     */
    public StairCase(int number) {
        this.number = number;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTraversable() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StairCase) {
            return number == ((StairCase) obj).number;
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public String toString() {
        return "-" + number;
    }
}
