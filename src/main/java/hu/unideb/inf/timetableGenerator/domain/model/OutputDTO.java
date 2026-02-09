package hu.unideb.inf.timetableGenerator.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OutputDTO {

    private final Week week;
    private final List<Course> courses;

    @Override
    public String toString() {
        return "OutputDTO{\n" +
                "week=" + week +
                ",\ncourses=\n" +
                String.join(System.lineSeparator(), courses.stream().map(Course::toString).toList()) +
                "}";
    }
}
