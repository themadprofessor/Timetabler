package me.timetabler.data.sql;

/**
 * Created by stuart on 11/03/16.
 */
public class JoinClause {
    public JoinType type;
    public String table;
    public String joinCondition;

    public JoinClause() {
    }

    public JoinClause(JoinType type, String table, String joinCondition) {
        this.type = type;
        this.table = table;
        this.joinCondition = joinCondition;
    }

    /**
     * Returns this join clause in a form ready to inserted into a SQL statement.
     * @return A SQL representation of this join clause.
     */
    @Override
    public String toString() {
        return new StringBuilder().append(type).append(' ').append(table).append(" ON ").append(joinCondition).toString();
    }
}
