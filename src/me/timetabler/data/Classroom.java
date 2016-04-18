package me.timetabler.data;

/**
 * A classroom at the school.
 */
public class Classroom implements DataType {
    /**
     * The id of the classroom. This must unique across all classrooms.
     */
    public int id;

    /**
     * The name of the classroom.
     */
    public String name;

    /**
     * The building where the classroom is located.
     */
    public Building building;

    /**
     * The subject taught in the classroom.
     */
    public Subject subject;

    /**
     * The default constructor, which does not initialise the members of the object.
     */
    public Classroom() {
    }

    /**
     * A constructor which initialises the members of the object, with the given parameters.
     * @param id The id of the classroom.
     * @param name The name of the classroom.
     * @param building The building where the classroom is located.
     * @param subject The subject taught in the classroom.
     */
    public Classroom(int id, String name, Building building, Subject subject) {
        this.id = id;
        this.name = name;
        this.building = building;
        this.subject = subject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Classroom classroom = (Classroom) o;

        return id == classroom.id && name.equals(classroom.name) && building.equals(classroom.building) && subject.equals(classroom.subject);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + building.hashCode();
        result = 31 * result + subject.hashCode();
        return result;
    }
}
