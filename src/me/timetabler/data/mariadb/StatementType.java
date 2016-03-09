package me.timetabler.data.mariadb;

import java.util.Map;

/**
 * The types of SQL statement which can be used. They are defined only with the SQL commands without values.
 */
public enum StatementType {
    SELECT_ALL("SELECT %s FROM %s;"), SELECT("SELECT %s FROM %s WHERE %s;"), UPDATE("UPDATE %s SET %s WHERE %s;"),
    DELETE("DELETE FROM %s WHERE %s;"), INSERT("INSERT INTO %s (%s) VALUES (%s)"),
    GET_LAST_AUTO_INCRE("SELECT LAST_INSERT_ID();"), SELECT_ALL_JOIN("SELECT %s FROM %s INNER JOIN %s ON %s;"),
    SELECT_JOIN("SELECT %s FROM %s INNER JOIN %s ON %s WHERE %s;");

    /**
     * The sql statement represented by this enum.
     */
    private String sql;

    StatementType(String sql) {
        this.sql = sql;
    }

    /**
     * Returns the sql represented by this enum in a form for a PreparedStatement to use. The values from the map will
     * replace the required parts from the generic SQL commands. The values from the map should contain '?'s for later
     * substitution by PreparedStatement, and not to substituted here!</br></br>
     * The list of keys is:
     * <ul>
     *     <li>table</li>
     *     <li>table2</li>
     *     <li>columns</li>
     *     <li>set</li>
     *     <li>where</li>
     *     <li>values</li>
     *     <li>join_key</li>
     * </ul>
     * <br>The default commands are:
     * <ul>
     *     <li>SELECT columns FROM table;</li>
     *     <li>SELECT columns FROM table WHERE where;</li>
     *     <li>UPDATE table SET set WHERE where;</li>
     *     <li>DELETE FROM table WHERE where;</li>
     *     <li>INSERT INTO table (columns) VALUES values;</li>
     *     <li>SELECT columns FROM table INNER JOIN table2 ON join_key;</li>
     *     <li>SELECT columns FROM table INNER JOIN table2 ON join_key WHERE where;</li>
     * </ul>
     * @param replace A map which contains the text to be replaced in the generic SQL commands. The keys are table, columns,
     *                set, where, and values.
     * @return Returns the SQL represented by this enum, ready to be used in a PreparedStatement.
     */
    public String getSql(Map<String, String> replace) {
        switch (this) {
            case SELECT_ALL:
                return String.format(sql, replace.get("columns"), replace.get("table"));
            case UPDATE:
                return String.format(sql, replace.get("table"), replace.get("set"), replace.get("where"));
            case DELETE:
                return String.format(sql, replace.get("table"), replace.get("where"));
            case INSERT:
                return String.format(sql, replace.get("table"), replace.get("columns"), replace.get("values"));
            case SELECT:
                return String.format(sql, replace.get("columns"), replace.get("table"), replace.get("where"));
            case GET_LAST_AUTO_INCRE:
                return sql;
            case SELECT_ALL_JOIN:
                return String.format(sql, replace.get("columns"), replace.get("table"), replace.get("table2"), replace.get("join_key"));
            case SELECT_JOIN:
                return String.format(sql, replace.get("columns"), replace.get("table"), replace.get("table2"), replace.get("join_key"), replace.get("where"));
            default:
                return null;
        }
    }
}
