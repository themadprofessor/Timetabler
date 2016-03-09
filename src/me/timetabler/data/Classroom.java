package me.timetabler.data;

/**
 * Created by stuart on 07/03/16.
 */
public class Classroom {
    public int id;
    public String name;
    public Building building;
    public Subject subject;

    public Classroom() {
    }

    public Classroom(int id, String name, Building building, Subject subject) {
        this.id = id;
        this.name = name;
        this.building = building;
        this.subject = subject;
    }
}
