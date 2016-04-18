package me.timetabler.data;

/**
 * A set which represents the ability of the students taught in a lesson.
 */
public class LearningSet implements DataType {
    /**
     * The id of this learningSet. This must be unique across all learningSets.
     */
    public int id;

    /**
     * The name of this learningSet.
     */
    public String name;

    /**
     * The default constructor, which does not initialise the members of the object.
     */
    public LearningSet() {
    }

    /**
     * A constrictor which initialises the members of the objects, with the given parameters.
     * @param id The id of the learningSet.
     * @param name The name of the learningSet.
     */
    public LearningSet(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
