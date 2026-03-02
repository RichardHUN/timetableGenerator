package hu.unideb.inf.timetableGenerator.dto;

import hu.unideb.inf.timetableGenerator.domain.model.PlannedCourse;
import hu.unideb.inf.timetableGenerator.domain.model.Preference;
import hu.unideb.inf.timetableGenerator.domain.model.Room;
import hu.unideb.inf.timetableGenerator.domain.model.Week;
import lombok.*;

import java.util.List;

/**
 * DTO class that defines the input of the timetable generation.
 * The week object is calculated from the planned courses,
 * so it is not necessary to provide it in the input, for that use
 * {@link SimpleInputDTO}.
 */
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
