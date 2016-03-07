package me.timetabler.data.dao;

import me.timetabler.data.SchoolClass;
import me.timetabler.data.Subject;

import java.util.List;
import java.util.Optional;

/**
 * The interface between a data source and the program. The dao will manipulate school class data.
 */
public interface SchoolClassDao {

    /**
     * Returns a list of all the class. If there are no class, an empty list will be returned. The type of list is to be determined by the implementation.
     * @return A list of all the class, which can be empty.
     */
    List<SchoolClass> getAllClasses();

    /**
     * Returns a list of all the classes which are of the given subject. The list will be empty if there are no classes
     * with the given subject.
     * @param subject The subject to list the classes.
     * @return The list of classes, which can be empty.
     */
    List<SchoolClass> getAllBySubject(Subject subject);

    /**
     * Returns an optional which can contain the class which corresponds with the id.
     * @param id The id to find the corresponding class.
     * @return An optional which can contain the class.
     */
    Optional<SchoolClass> getById(int id);

    /**
     * Inserts the given SchoolClass into the data source and returns the id of the SchoolClass.
     * @param schoolClass
     * @return
     */
    int insertClass(SchoolClass schoolClass);
    boolean updateClass(SchoolClass schoolClass);
    boolean deleteClass(SchoolClass schoolClass);
}
