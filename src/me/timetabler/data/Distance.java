package me.timetabler.data;

/**
 * A distance between two classrooms.
 */
public class Distance implements DataType {
    /**
     * The id of this distance. This must unique across all distances.
     */
    public int id;

    /**
     * The classroom where the distance was measured from.
     */
    public Classroom startRoom;

    /**
     * The classroom where the distance was measured to.
     */
    public Classroom endRoom;

    /**
     * The distance between the two classroom. The units are of arbitrary size.
     */
    public int distance;

    /**
     * The default constructor, which does not initialise the members of the object.
     */
    public Distance() {
    }

    /**
     * A constructor which initialises the members of the object, with the given parameters.
     * @param id The id of the distance.
     * @param startRoom The classroom where the distance was measured from.
     * @param endRoom The classroom where the distance was measured to.
     * @param distance The distance between the two classrooms.
     */
    public Distance(int id, Classroom startRoom, Classroom endRoom, int distance) {
        this.id = id;
        this.startRoom = startRoom;
        this.endRoom = endRoom;
        this.distance = distance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Distance distance1 = (Distance) o;

        return id == distance1.id
                && distance == distance1.distance
                && (startRoom.equals(distance1.startRoom) && endRoom.equals(distance1.endRoom))
                || (startRoom.equals(distance1.endRoom) && endRoom.equals(distance1.startRoom));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + startRoom.hashCode();
        result = 31 * result + endRoom.hashCode();
        result = 31 * result + distance;
        return result;
    }
}
