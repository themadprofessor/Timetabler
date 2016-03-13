package me.timetabler.data;

/**
 * Created by stuart on 13/03/16.
 */
public class Distance {
    public int id;
    public Classroom startRoom;
    public Classroom endRoom;
    public int distance;

    public Distance() {
    }

    public Distance(int id, Classroom startRoom, Classroom endRoom, int distance) {
        this.id = id;
        this.startRoom = startRoom;
        this.endRoom = endRoom;
        this.distance = distance;
    }
}
