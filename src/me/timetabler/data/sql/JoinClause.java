package me.timetabler.data.sql;

/**
 * Created by stuart on 11/03/16.
 */
public class JoinClause {
    protected JoinType type;
    protected String table;
    protected String joinCondition;

    public JoinClause() {
    }

    public JoinClause(JoinType type, String table, String joinCondition) {
        this.type = type;
        this.table = table;
        this.joinCondition = joinCondition;
    }

    public JoinType getType() {
        return type;
    }

    public void setType(JoinType type) {
        this.type = type;
    }

    public String getJoinCondition() {
        return joinCondition;
    }

    public void setJoinCondition(String joinCondition) {
        this.joinCondition = joinCondition;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
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
