package me.timetabler.data;

/**
 * A subject at the school.
 */
public class Subject implements DataType {
    /**
     * The name of the subject. This must be unique across all staff.
     */
    public String name;

    /**
     * The unique subject id.
     */
    public int id;

    /**
     * A default constructor.
     */
    public Subject() {
    }

    /**
     * A constructor which initialises all members of the object.
     * @param id The id of the subject.
     * @param name The name of the subject.
     */
    public Subject(int id, String name){
        this.name = name;
        this.id = id;
    }
}
