package me.timetabler.data.dao;

import me.timetabler.data.Subject;
import me.timetabler.data.mariadb.MariaSubjectDao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The interface between a data source and the program. This dao will manipulate subject data. A static factory method
 * is provided to return the correct implementation based on the config map given.
 */
public interface SubjectDao extends AutoCloseable {
    /**
     * Returns a list of all the subjects. If there are no subjects, an empty list will be returned. The type of list is to be determined by the implementation.
     * @return A list of all the subjects, which can be empty.
     */
    List<Subject> getAllSubjects();

    /**
     * Returns the subject which has the given id. The optional will be empty if the id does not reference any subjects.
     * @param id The id of the subject to be found.
     * @return An optional containing the subject if it exists, or empty if it does not.
     */
    Optional<Subject> getById(int id);

    /**
     * Inserts the given subject in to the data store. If the subject was successfully entered in to the data store, it will return true, and false if it could not.
     * @param subject The subject to be added to the data store.
     * @return Returns true if the subject was successfully added to the data store, and false if it failed.
     */
    boolean insertSubject(Subject subject);

    /**
     * Updates the given subject in the data store. The id of the given subject will be the entry in the store to be updated with the name of the given subject
     * It returns true if the entry was successfully updated and false if it failed.
     * @param subject The entry to be updated. The entry with the id of the given subject will be updated with the given subject's name.
     * @return True if the entry successfully updated and false if it failed.
     */
    boolean updateSubject(Subject subject);

    /**
     * Deletes the given entry from the data source. This returns true if the entry as successfully removed from the data source and false if it failed.
     * @param subject The entry to to be deleted from the data source.
     * @return Returns true if the entry was successfully removed from the data source and false if it fails.
     */
    boolean deleteSubject(Subject subject);

    /**
     * A factory method to return the correct implementation for the given config map. If the type is not in the map or is an unimplmented type, this will return null.
     * @param config The config map, which should contain an entry 'type'.
     * @return The corresponding implementation of this class, or null if there is no 'type' entry or is an unimplemented type..
     */
    static SubjectDao getDao(Map<String, String> config) {
        String type = config.get("type");
        switch (type) {
            case "MARIADB": return new MariaSubjectDao(config);
            default: return null;
        }
    }
}
