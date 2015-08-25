package me.timetabler;

/**
 * Created by stuart on 24/08/15.
 */
public class Coordinates {
    public int x, y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "" + x + "," + y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinates) {
            return x == ((Coordinates) obj).x && y == ((Coordinates) obj).y;
        } else {
            return super.equals(obj);
        }
    }
}
