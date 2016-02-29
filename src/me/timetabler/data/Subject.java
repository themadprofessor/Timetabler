package me.timetabler.data;

/**
 * A subject at the school.
 */
public class Subject {
    /**
     * The name of the subject.
     */
    public String name;

    /**
     * The unique subject id.
     */
    public int id;

    public Subject() {
    }

    public Subject(int id, String name){
        this.name = name;
        this.id = id;
    }
}
