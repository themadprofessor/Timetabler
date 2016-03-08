package me.timetabler.data;

/**
 * Created by stuart on 07/03/16.
 */
public class Classroom {
    public int id;
    public String name;
    public String buildingName;
    public Subject subject;

    public Classroom() {
    }

    public Classroom(int id, String name, String buildingName, Subject subject) {
        this.id = id;
        this.name = name;
        this.buildingName = buildingName;
        this.subject = subject;
    }
}
