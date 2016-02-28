package me.timetabler.sql;

import javafx.scene.control.Alert;
import me.timetabler.data.Subject;
import me.timetabler.ui.JavaFxBridge;
import me.util.Log;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by stuart on 26/02/16.
 */
public class SubjectDao {
    private Database database;
    private PreparedStatement updateStatement;
    private PreparedStatement insertStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement selectStatement;
    private PreparedStatement selectAllStatement;

    public SubjectDao(Database database) {
        this.database = database;
    }

    public int updateEntry(Subject subject) {
        int changes;
        try {
            if (updateStatement == null) {
                initUpdate();
            } else {
                updateStatement.clearParameters();
                updateStatement.clearBatch();
            }
            updateStatement.setString(1, subject.name);
            updateStatement.setInt(2, subject.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseAccessException!");
            throw new DatabaseAccessException(e);
        }

        try {
            changes = updateStatement.executeUpdate();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseUpdateException!");
            throw new DatabaseUpdateException(e);
        }
        return changes;
    }

    public int[] batchUpdate(List<Subject> subjects) {
        int[] changes;
        try {
            if (updateStatement == null) {
                initUpdate();
            } else {
                updateStatement.clearParameters();
                updateStatement.clearBatch();
            }
            subjects.forEach(subject -> {
                try {
                    updateStatement.setString(1, subject.name);
                    updateStatement.setInt(2, subject.id);
                    updateStatement.addBatch();
                } catch (SQLException e) {
                    Log.debug("Caught [" + e + "] so throwing DatabaseAccessException!");
                    throw new DatabaseAccessException(e);
                }
            });
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseAccessException!");
            throw new DatabaseAccessException(e);
        }

        try {
            changes = updateStatement.executeBatch();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseUpdateException!");
            throw new DatabaseUpdateException(e);
        }

        return changes;
    }

    public int insertEntry(Subject subject) {
        int change;

        try {
            if (insertStatement == null) {
                initInsert();
            } else {
                insertStatement.clearParameters();
                insertStatement.clearBatch();
            }

            insertStatement.setInt(1, subject.id);
            insertStatement.setString(1, subject.name);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseAccessException!");
            throw new DatabaseAccessException(e);
        }

        try {
            change = insertStatement.executeUpdate();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseUpdateException!");
            throw new DatabaseUpdateException(e);
        }

        return change;
    }

    public int[] batchInsert(List<Subject> subjects) {
        try {

        }
    }

    private void initUpdate() {
        try {
            updateStatement = database.createPrepStatement("UPDATE subject SET name=? WHERE id=?;");
        } catch (DatabaseConnectionException e) {
            catchDbConException(e);
        }
    }

    private void initInsert() {
        try {
            insertStatement = database.createPrepStatement("INSERT INTO subject (id,name) VALUES (?,?);");
        } catch (DatabaseConnectionException e) {
            catchDbConException(e);
        }
    }

    private void initDelete() {
        try {
            deleteStatement = database.createPrepStatement("DELETE FROM subject WHERE id=?;");
        } catch (DatabaseConnectionException e) {
            catchDbConException(e);
        }
    }

    private void initSelectAll() {
        try {
            selectAllStatement = database.createPrepStatement("SELECT * FROM subject;");
        } catch (DatabaseConnectionException e) {
            catchDbConException(e);
        }
    }

    private void initSelect() {
        try {
            selectStatement = database.createPrepStatement("SELECT id=? FROM subject;");
        } catch (DatabaseConnectionException e) {
            catchDbConException(e);
        }
    }

    private void catchDbConException(DatabaseConnectionException e) {
        Log.error(e);
        JavaFxBridge.createAlert(Alert.AlertType.ERROR, "A Fatal Error Has Occurred!", "Failed to connect to database server!", "If this is the first time you have seen this error, restart the program as this should restart the server.\nThe system could not establish a connect to the database server. Is it running?\nCheck the current running processes for \"mysql\" or \"mariadb\" (Task Manger).", true);
    }
}
