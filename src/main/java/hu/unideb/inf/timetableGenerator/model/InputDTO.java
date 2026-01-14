package hu.unideb.inf.timetableGenerator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class InputDTO {

    private final Week week;
    private final List<Room> rooms;
    private final List<PlannedCourse> plannedCourses;
    private final List<Preference> preferences;

}
