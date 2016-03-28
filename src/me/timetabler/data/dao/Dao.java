package me.timetabler.data.dao;

import me.timetabler.data.exceptions.DataAccessException;

import java.util.List;

/**
 * The way for the program to interact with a data source.
 */
public interface Dao<T> {
    List<T> getAll() throws DataAccessException;
}
