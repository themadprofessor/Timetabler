package me.timetabler.ui;

import com.google.gson.Gson;
import me.timetabler.data.SchoolClass;
import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataConnectionException;
import me.timetabler.data.exceptions.DataExceptionHandler;
import me.timetabler.data.exceptions.DataUpdateException;
import me.util.Log;
import netscape.javascript.JSObject;

/**
 * The bridge between the Javascript and the Java objects.
 */
public class Bridge {
    private DaoManager daoManager;
    private JSObject bridge;
    private Gson gson;

    public Bridge(DaoManager daoManager, JSObject bridge) {
        this.daoManager = daoManager;
        gson = new Gson();
        this.bridge = bridge;
    }

    public void out(String msg) {
        Log.info("[JAVASCRIPT ]" + msg);
    }

    public void debug(String msg) {
        Log.debug("[JAVASCRIPT] " + msg);
    }

    public void err(String msg) {
        Log.error("[JAVASCRIPT] " + msg);
    }

    public void add(String type, String json) {
        switch (type) {
            case "Class":
            case "class":
                SchoolClass clazz = gson.fromJson(json, SchoolClass.class);

                try {
                    clazz.id = daoManager.getSchoolClassDao().insertClass(clazz);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "class", false);
                    //TODO: remove from ui
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
            case "Subject":
            case "subject":
                Subject subject = gson.fromJson(json, Subject.class);

                try {
                    subject.id = daoManager.getSubjectDao().insertSubject(subject);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "subject", false);
                    //TODO: remove from ui
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
            case "Staff":
            case "staff":
                Staff st = gson.fromJson(json, Staff.class);

                try {
                    st.id = daoManager.getStaffDao().insertStaff(st);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "staff", false);
                    //TODO: remove from ui
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
        }
    }

    public void update(String type, String json) {

    }

    public void remove(String type, String json) {
        switch (type) {
            case "Class":
            case "class":
                try {
                    daoManager.getSchoolClassDao().deleteClass(gson.fromJson(json, SchoolClass.class));
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "class", false);
                    //TODO: remove from ui
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
            case "Subject":
            case "subject":
                try {
                    daoManager.getSubjectDao().deleteSubject(gson.fromJson(json, Subject.class));
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "subject", false);
                    //TODO: remove from ui
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
            case "Staff":
            case "staff":
                try {
                    daoManager.getStaffDao().deleteStaff(gson.fromJson(json, Staff.class));
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "staff", false);
                    //TODO: remove from ui
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
        }
    }
}
