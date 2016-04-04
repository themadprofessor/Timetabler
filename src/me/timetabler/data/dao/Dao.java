package me.timetabler.data.dao;

import me.timetabler.data.exceptions.DataAccessException;

import java.util.List;

/**
 * The way for the program to interact with a data source.
 */
public interface Dao<T> extends AutoCloseable {
    /**
     * Returns a list of all the data of type T. If there is no acceptable data, an empty list will be returned.
     * The type of list is to be determined by the implementation.
     *
     * @return A list of all the data of type T, which can be empty.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<T> getAll() throws DataAccessException;
}
