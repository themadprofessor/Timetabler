package me.timetabler.data.mariadb;

/**
 * The types of SQL statement which can be used. They are defined only with the SQL commands without values.
 */
public enum StatementType {
    SELECT("SELECT %s FROM %s;"), SELECT_ALL("SELECT * FROM %s"), UPDATE("UPDATE %s SET %s WHERE %s;"), DELETE("DELETE FROM %s WHERE %s;"), INSERT("INSERT INTO %s (%s) VALUES (%s)");

    /**
     * The sql statement represented by this enum.
     */
    private String sql;

    StatementType(String sql) {
        this.sql = sql;
    }

    /**
     * Returns the sql represented by this enum in a form for a PreparedStatement to use. The only required parameter is
     * table. The others are only required if the sql requires them. For example, SELECT will require table and where,
     * whereas INSERT will require table, columns and values.
     * @param table The table which the statement will be executed on. REQUIRED.
     * @param where The where clause of the statement. For example, DELETE FROM table WHERE where;. The question mark
     *              SQL replacement character is to used where applicable.
     * @param set The set clause of the UPDATE statement. The question mark SQL replacement character is to be used
     *            where applicable.
     * @param columns The columns to be changed in the INSERT statement. For example, INSERT INTO table (columns) VALUES
     *                values;. The question mark SQL replacement character is to be used where applicable.
     * @param values The values to be changed in the the INSERT statement. For example, INSERT INTO table (columns)
     *               VALUES values;. The question mark SQL replacement character is to be used where applicable.
     * @return Returns the SQL represented by this enum, ready to be used in a PreparedStatement.
     */
    public String getSql(String table, String where, String set, String columns, String values) {
        switch (this) {
            case SELECT:
                return String.format(sql, where, table);
            case SELECT_ALL:
                return String.format(sql, table);
            case UPDATE:
                return String.format(sql, table, set, where);
            case DELETE:
                return String.format(sql, table, where);
            case INSERT:
                return String.format(sql, table, columns, values);
            default:
                return null;
        }
    }
}
