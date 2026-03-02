package hu.unideb.inf.timetableGenerator.dto;

import hu.unideb.inf.timetableGenerator.domain.model.Course;
import hu.unideb.inf.timetableGenerator.domain.model.Week;

import java.util.List;

/**
 * Represents the output of the timetable generation process, containing the list of planned courses
 * and the same week object as the input, which is used for timetable visualization.
 * @param week
 * @param courses
 */
public record OutputDTO(Week week, List<Course> courses) {

    @Override
    public String toString() {
        return "OutputDTO{\n" +
                "week=" + week +
                ",\ncourses=\n" +
                String.join(System.lineSeparator(), courses.stream().map(Course::toString).toList()) +
                "}";
    }
}
