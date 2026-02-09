package hu.unideb.inf.timetableGenerator.model;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class InputDTO {

    private Week week;
    private List<Room> rooms;
    private List<PlannedCourse> plannedCourses;
    private List<Preference> preferences;

}
