package me.timetabler.data;

/**
 * A building at the school.
 */
public class Building implements DataType {
    /**
     * The id of the building. This must be unique across all buildings.
     */
    public int id;

    /**
     * The name of the building.
     */
    public String buildingName;

    /**
     * The default constructor which does not initialise the members of the object.
     */
    public Building() {
    }

    /**
     * A constructor which initialises the members of the object with the given parameters.
     * @param id The id of the building.
     * @param buildingName The name of the building.
     */
    public Building(int id, String buildingName) {
        this.id = id;
        this.buildingName = buildingName;
    }
}
