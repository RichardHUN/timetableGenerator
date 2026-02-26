package hu.unideb.inf.timetableGenerator.web.timetables;

import hu.unideb.inf.timetableGenerator.entity.TimetableEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
public interface TimetablesController {

    @GetMapping("/timetables")
    @PreAuthorize("isAuthenticated()")
    List<TimetableEntity> getAllTimetables();

    @DeleteMapping("/timetables/{id}")
    @PreAuthorize("isAuthenticated()")
    void deleteTimetable(@PathVariable int id);

    @PatchMapping("/timetables/{id}")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<?> renameTimetable(@PathVariable int id, @RequestParam String name);

}
