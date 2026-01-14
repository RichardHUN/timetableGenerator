package hu.unideb.inf.timetableGenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OutputDTO {

    private final Week week;
    private final List<Course> courses;

}
