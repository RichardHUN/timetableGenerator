package hu.unideb.inf.timetableGenerator.dto;

import hu.unideb.inf.timetableGenerator.domain.model.PlannedCourse;
import hu.unideb.inf.timetableGenerator.domain.model.Preference;
import hu.unideb.inf.timetableGenerator.domain.model.Room;
import hu.unideb.inf.timetableGenerator.domain.model.Week;
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
