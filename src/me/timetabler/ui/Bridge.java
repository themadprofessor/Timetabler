package me.timetabler.ui;

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

    public Bridge(DaoManager daoManager, JSObject bridge) {
        this.daoManager = daoManager;
        this.bridge = bridge;
    }

    public void out(String msg) {
        Log.info("[JAVASCRIPT] " + msg);
    }

    public void debug(String msg) {
        Log.debug("[JAVASCRIPT] " + msg);
    }

    public void err(String msg) {
        Log.error("[JAVASCRIPT] " + msg);
    }

    public void verbose(String msg) {
        Log.verbose("[JAVASCRIPT] " + msg);
    }

    public int add(String type, String data) {
        switch (type) {
            case "Subject":
            case "subject":
                Subject subject = new Subject();
                subject.name = data;

                try {
                    subject.id = daoManager.getSubjectDao().insertSubject(subject);
                    bridge.call("window.success = true;");
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "subject", false);
                    bridge.call("window.success = false;");
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                return subject.id;
            case "Staff":
            case "staff":
                Staff st = new Staff();
                try {
                    String[] split = data.replace("[", "").replace("]", "").split(",");
                    st.name = split[0];

                    st.subject = daoManager.getSubjectDao().getById(Integer.parseInt(split[1])).get();
                    st.hoursPerWeek = Integer.parseInt(split[2]);
                    st.id = daoManager.getStaffDao().insertStaff(st);
                    Log.debug("Added Staff Member [" + st.name + ']');
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "staff", false);
                    bridge.call("window.success = false");
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }
                return st.id;
            default:
                return -1;
        }
    }

    public void update(String type, String json) {

    }

    public void remove(String type, String json) {
        switch (type) {
            case "Subject":
            case "subject":
                try {
                    Log.debug("Removing Subject.");
                    Log.verbose("Subject JSON[" + json + ']');
                    Subject subject = new Subject();
                    subject.id = Integer.parseInt(json.split(",")[0].replace(']', '\0'));
                    daoManager.getSubjectDao().deleteSubject(subject);
                    bridge.setMember("success", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "subject", false);
                    bridge.setMember("success", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
            case "Staff":
            case "staff":
                try {
                    Log.debug("Removing Staff.");
                    Log.verbose("Staff JSON [" + json + ']');
                    Staff staff = new Staff();
                    staff.id = Integer.parseInt(json.split(",")[0].replace(']', '\0'));
                    daoManager.getStaffDao().deleteStaff(staff);
                    bridge.setMember("success", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "staff", false);
                    bridge.setMember("success", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
        }
    }
}
