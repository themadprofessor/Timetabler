package me.timetabler.data.dao;

import me.timetabler.data.Staff;
import me.timetabler.data.Subject;

import java.util.List;
import java.util.Optional;

/**
 * Created by stuart on 28/02/16.
 */
public interface StaffDao {
    List<Staff> getAllStaff();
    List<Staff> getAllBySubject(Subject subject);
    Optional<Staff> getById(int id);

    int insertStaff(Staff staff);
    boolean updateStaff(Staff staff);
    boolean deleteStaff(Staff staff);
}
