package me.timetabler.data.sql;

/**
 * The possible type of SQL joins.
 */
public enum  JoinType {
    /**
     * Represents a SQL inner join.
     */
    INNER,

    /**
     * Represents a SQL left join.
     */
    LEFT,

    /**
     * Represents a SQL right join.
     */
    RIGHT,

    /**
     * Represents a SQL full join.
     */
    FULL;

    /**
     * Returns the name of the join in a form ready to be used in an SQL statement.
     * @return The name of the join.
     */
    @Override
    public String toString() {
        switch (this) {
            case INNER:
                return "INNER JOIN";
            case LEFT:
                return "LEFT JOIN";
            case RIGHT:
                return "RIGHT JOIN";
            case FULL:
                return "FULL";
            default:
                return null;
        }
    }
}
