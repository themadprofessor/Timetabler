package me.timetabler.data.sql;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A builder which builds SQL statements form the given values.
 */
public class SqlBuilder {
    private ArrayList<String> columns;
    private String fromTable;
    private StatementType type;
    private String where;
    private ArrayList<String> values;
    private ArrayList<String> set;
    private ArrayList<JoinClause> joins;

    /**
     * Initialises the SQL builder and sets the required values.
     * @param table The table which will be used in the statement.
     * @param type The type of SQL statement.
     */
    public SqlBuilder(String table, StatementType type) {
        this.type = type;
        fromTable = table;
    }

    /**
     * Sets the table to be used in the statement.
     * @param table The table which will be used in the statement.
     * @return This builder instance.
     */
    public SqlBuilder setFromTable(String table) {
        fromTable = table;
        return this;
    }

    /**
     * Adds a column to the statement. This will be used if the statement type requires it. The column must contain the
     * table name if used with a join in the form <i>table.column</i>.
     * @param column The column to be added to the statement which must have the table name if a join is used.
     * @return This builder instance.
     */
    public SqlBuilder addColumn(String column) {
        if (columns == null) {
            columns = new ArrayList<>();
        }
        columns.add(column);
        return this;
    }

    /**
     * Adds the given columns to the statement. They will be used if the statement type requires it. The columns must
     * contain the table name if used with a join in the form <i>table.columns</i>.
     * @param columns The columns to be added to the statement which must have the table name if a join is used.
     * @return This builder instance.
     */
    public SqlBuilder addColumns(String... columns) {
        if (this.columns == null) {
            this.columns = new ArrayList<>();
        }

        Collections.addAll(this.columns, columns);
        return this;
    }

    /**
     * Sets the statement type of this statement.
     * @param type The statement type of this statement.
     * @return This builder instance.
     */
    public SqlBuilder setStatementType(StatementType type) {
        this.type = type;
        return this;
    }

    /**
     * Adds a where clause to the statement. It should be in the form <i>column operator value</i>. It can contain the
     * placeholder character '?' if the statement will be used with a PreparedStatement or CallableStatement.
     * @param clause The where clause to be added, which can contain the placeholder character '?'.
     * @return This builder instance.
     */
    public SqlBuilder addWhereClause(String clause) {
        where = clause;
        return this;
    }

    /**
     * Adds a value to the statement. This will only be used with the insert statement. It can be the placeholder
     * character '?' if the statement will be used with a PreparedStatement or CallableStatement.
     * @param value The value to be added, which can be the placeholder character '?'.
     * @return This builder instance.
     */
    public SqlBuilder addValue(String value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(value);
        return this;
    }

    /**
     * Adds the given values to the statement. This will only be used with the update statement. It can contain the
     * placeholder character '?' if the statement will be used with a PreparedStatement or CallableStatement.
     * @param values The values to be added, which can contain the placeholder character '?'.
     * @return This builder instance.
     */
    public SqlBuilder addValues(String... values) {
        if (this.values == null) {
            this.values = new ArrayList<>();
        }
        Collections.addAll(this.values, values);
        return this;
    }

    /**
     * Adds a set clause to this statement. This will only be only used with the update statement. It should be in the
     * form <i>column=value</i> where value can be the placeholder value '?' if the statement will be used with a
     * PreparedStatement or CallableStatement.
     * @param clause The set clause to be added, which can contain the placeholder character '?'.
     * @return This builder instance.
     */
    public SqlBuilder addSetClause(String clause) {
        if (set == null) {
            set = new ArrayList<>();
        }
        set.add(clause);
        return this;
    }

    /**
     * Adds the given set statements to this statement. This will only be only used with the update statement. It should
     * be in the form <i>column=value</i> where value can be the placeholder character '?' if the statement will be used
     * with a PreparedStatement or CallableStatement.
     * @param clauses The clauses to be added, which can contain the placeholder character '?'.
     * @return This builder instance.
     */
    public SqlBuilder addSetClauses(String... clauses) {
        if (set == null) {
            set = new ArrayList<>();
        }
        Collections.addAll(set, clauses);
        return this;
    }

    /**
     * Adds a join clause to this statement. This will only be used with the insert statement.
     * @param clause The join clause to be added.
     * @return This builder instance.
     */
    public SqlBuilder addJoinClause(JoinClause clause) {
        if (joins == null) {
            joins = new ArrayList<>();
        }
        joins.add(clause);
        return this;
    }

    /**
     * Builds an SQL statement from the parameters specified previously.<br>
     * Each statement type uses different parameters as shown, ones in bold are required:<br>
     * <ul>
     *     <li>SELECT
     *         <ul>
     *             <li><b>From Table</b></li>
     *             <li><b>Statement Type</b></li>
     *             <li>Columns. If not present, all columns will be selected
     *             </li>
     *             <li>Join Clauses</li>
     *             <li>Where Clauses</li>
     *         </ul>
     *     </li>
     *     <li>INSERT
     *         <ul>
     *             <li><b>From Table</b></li>
     *             <li><b>Statement Type</b></li>
     *             <li>Columns</li>
     *             <li><b>Values</b></li>
     *         </ul>
     *     </li>
     *     <li>DELETE
     *         <ul>
     *             <li><b>From Table</b></li>
     *             <li><b>Statement Type</b></li>
     *             <li>Where Clauses</li>
     *         </ul>
     *     </li>
     *     <li>UPDATE
     *         <ul>
     *             <li><b>From Table</b></li>
     *             <li><b>Statement Type</b></li>
     *             <li><b>Set Clauses</b></li>
     *             <li>Where Clauses</li>
     *         </ul>
     *     </li>
     * </ul>
     * @return The SQL statement.
     * @throws IllegalStateException Thrown if the from table or statement type is not specified.
     */
    public String build() {
        if (fromTable == null) {
            throw new IllegalStateException("From table has not be specified!");
        }
        if (type == null) {
            throw new IllegalStateException("Statement type has not be specified!");
        }

        StringBuilder builder = new StringBuilder();
        switch (type) {
            case SELECT:
                builder.append("SELECT ");
                if (columns == null || columns.size() == 0) {
                    builder.append('*');
                } else {
                    columns.forEach(column -> builder.append(column).append(','));
                    builder.deleteCharAt(builder.length()-1);
                }

                builder.append(" FROM ").append(fromTable);

                if (joins != null && joins.size() != 0) {
                    builder.append(' ');
                    joins.forEach(joinClause -> builder.append(joinClause).append(' '));
                    builder.deleteCharAt(builder.length()-1);
                }

                if (where != null) {
                    builder.append(" WHERE ").append(where);
                }

                builder.append(';');
                break;
            case DELETE:
                builder.append("DELETE FROM ").append(fromTable);

                if (where != null) {
                    builder.append(" WHERE ").append(where);
                }

                builder.append(';');
                break;
            case INSERT:
                builder.append("INSERT INTO ").append(fromTable).append(' ');

                if (columns != null && columns.size() != 0) {
                    builder.append('(');
                    columns.forEach(column -> builder.append(column).append(','));
                    builder.deleteCharAt(builder.length() - 1).append(") ");
                }

                builder.append("VALUES ");
                values.forEach(value -> builder.append(value).append(','));
                builder.deleteCharAt(builder.length()-1).append(';');
                break;
            case UPDATE:
                builder.append("UPDATE ").append(fromTable).append(" SET ");
                set.forEach(setValue -> builder.append(setValue).append(','));
                builder.deleteCharAt(builder.length()-1);

                if (where != null) {
                    builder.append(" WHERE ").append(where);
                }

                builder.append(';');
                break;
        }

        return builder.toString();
    }

    /**
     * Calls the build method and returns its return value.
     * @return The SQL Statement.
     * @throws IllegalStateException Thrown if the from table or statement type are not specified.
     */
    @Override
    public String toString() {
        return build();
    }
}
