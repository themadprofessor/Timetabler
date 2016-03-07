package me.timetabler.data.dao;

import me.timetabler.data.Day;

import java.util.Optional;

/**
 * Created by stuart on 04/03/16.
 */
public interface DayDao {
    Optional<Day> getById(int id);
    Optional<Day> getByName(String name);
}