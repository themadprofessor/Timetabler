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

    /**
     * Returns this coordinate as a String in the for x,y.
     * @return This coordinate as a String.
     */
    @Override
    public String toString() {
        return "" + x + "," + y;
    }

    /*@Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate) {
            return x == ((Coordinate) obj).x && y == ((Coordinate) obj).y;
        } else {
            return super.equals(obj);
        }
    }*/

    /**
     * Returns true if the given Object is an instance of Coordinate and has the same x and y values, else returns false.
     * @param o The object to compare.
     * @return True if the given Object equals this object.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;                                //If the object reference is the same as this reference, return true.
        if (o == null || !(o instanceof Coordinate))
            return false;                               //If the object is null or not a Coordinate, return false.

        Coordinate that = (Coordinate) o;               //Cast to Coordinate object.

        return x == that.x && y == that.y;              //If this coordinate and that coordinate have the same x and y values, return true.

    }

    /**
     * Returns a hash of this object.
     * @return The hash of this object.
     */
    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;   //Create a hash value from the members of this object and a number.
        return result;
    }
}
