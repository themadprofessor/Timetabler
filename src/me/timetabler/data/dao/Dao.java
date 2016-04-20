package me.timetabler.data.dao;

import me.timetabler.data.exceptions.DataAccessException;

import java.util.List;
import java.util.Optional;

/**
 * The way for the program to interact a type of data from a data source. All list implementations are to be determined by the
 * implementation.
 */
public interface Dao<T> extends AutoCloseable {
    /**
     * Returns a list of all the data. If there is no acceptable data, an empty list will be returned.
     * The type of list is to be determined by the implementation.
     *
     * @return A list of all the data, which can be empty.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<T> getAll() throws DataAccessException;

    /**
     * Returns the data which has the given id. The optional will be empty if the id does not reference any data.
     *
     * @param id The id of the data to be found.
     * @return An optional containing the data if it exists, or empty if it does not.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<T> getById(int id) throws DataAccessException;
}
