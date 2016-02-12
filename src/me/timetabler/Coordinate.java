package me.timetabler;

/**
 * A way of storing an x and y value within one object, to be used with the SchoolMap.
 */
public class Coordinate {
    /**
     * The x and y values of the coordinate.
     */
    public int x, y;

    /**
     * Initialises the coordinate.
     * @param x The x value of the coordinate.
     * @param y The y value of the coordinate.
     *
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "" + x + "," + y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate) {
            return x == ((Coordinate) obj).x && y == ((Coordinate) obj).y;
        } else {
            return super.equals(obj);
        }
    }
}
