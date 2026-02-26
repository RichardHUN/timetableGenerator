package hu.unideb.inf.timetableGenerator.web.timetables;

import hu.unideb.inf.timetableGenerator.entity.TimetableEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api")
public interface TimetablesController {

    @GetMapping("/timetables")
    @PreAuthorize("isAuthenticated()")
    List<TimetableEntity> getAllTimetables();

    @DeleteMapping("/timetables/{id}")
    @PreAuthorize("isAuthenticated()")
    void deleteTimetable(@PathVariable int id);

}
