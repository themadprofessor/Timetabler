package me.timetabler.ui;

import me.timetabler.data.*;
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

    public void warn(String msg) {
        Log.warning("[JAVASCRIPT] " + msg);
    }

    public int add(String type, String data) {
        switch (type) {
            case "Subject":
            case "subject":
                Subject subject = new Subject();
                subject.name = data;

                try {
                    subject.id = daoManager.getSubjectDao().insertSubject(subject);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "subject", false);
                    bridge.call("setSuccess", false);
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
                    Subject sub = new Subject();
                    sub.id = Integer.parseInt(split[1]);

                    st.subject = sub;
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
            case "Year":
            case "year":
                SchoolYear year = new SchoolYear();
                year.schoolYearName = data;

                try {
                    year.id = daoManager.getSchoolYearDao().insert(year);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "year", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                return year.id;
            case "Set":
            case "set":
                LearningSet learningSet = new LearningSet();
                learningSet.name = data;

                try {
                    learningSet.id = daoManager.getLearningSetDao().insert(learningSet);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "learningSet", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                return learningSet.id;
            case "Class":
            case "class":
                SubjectSet subjectSet = new SubjectSet();
                String[] split = data.replace("[", "").replace("]", "").split(",");

                Subject sub = new Subject();
                sub.id = Integer.parseInt(split[0]);
                subjectSet.subject = sub;

                LearningSet set = new LearningSet();
                set.id = Integer.parseInt(split[1]);
                subjectSet.learningSet = set;

                SchoolYear schoolYear = new SchoolYear();
                schoolYear.id = Integer.parseInt(split[2]);
                subjectSet.schoolYear = schoolYear;

                try {
                    subjectSet.id = daoManager.getSubjectSetDao().insert(subjectSet);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "subjectSet", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                return subjectSet.id;
            default:
                return -1;
        }
    }

    public void update(String type, String json) {

    }

    public void remove(String type, String data) {
        switch (type) {
            case "Subject":
            case "subject":
                try {
                    Log.debug("Removing Subject.");
                    Log.verbose("Subject [" + data + ']');
                    Subject subject = new Subject();
                    subject.id = Integer.parseInt(data.split(",")[0].replace("]", ""));
                    daoManager.getSubjectDao().deleteSubject(subject);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "subject", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
            case "Staff":
            case "staff":
                try {
                    Log.debug("Removing Staff.");
                    Log.verbose("Staff [" + data + ']');
                    Staff staff = new Staff();
                    staff.id = Integer.parseInt(data.split(",")[0].replace("]", ""));
                    daoManager.getStaffDao().deleteStaff(staff);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "staff", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
            case "Year":
            case "year":
                try {
                    Log.debug("Removing Year.");
                    Log.verbose("SchoolYear [" + data + ']');
                    SchoolYear year = new SchoolYear();
                    year.id = Integer.parseInt(data.split(",")[0].replace("]", ""));
                    daoManager.getSchoolYearDao().delete(year);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "year", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
            case "Set":
            case "set":
                try {
                    Log.debug("Removing LearningSet.");
                    Log.verbose("SchoolLearningSet [" + data + ']');
                    LearningSet learningSet = new LearningSet();
                    learningSet.id = Integer.parseInt(data.split(",")[0].replace("]", ""));
                    daoManager.getLearningSetDao().delete(learningSet);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "learningSet", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
            case "Class":
            case "class":
                try {
                    Log.debug("Removing SubjectSet.");
                    Log.verbose("SubjectSet [" + data + ']');
                    SubjectSet subjectSet = new SubjectSet();
                    subjectSet.id = Integer.parseInt(data.split(",")[0].replace("]", ""));
                    daoManager.getSubjectSetDao().delete(subjectSet);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "subjectSet", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
        }
    }
}
