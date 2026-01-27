package hu.unideb.inf.timetableGenerator.runner;

import hu.unideb.inf.timetableGenerator.generator.TimeTableGenerator;
import hu.unideb.inf.timetableGenerator.model.*;
import hu.unideb.inf.timetableGenerator.runner.parser.ArgParser;
import hu.unideb.inf.timetableGenerator.runner.parser.RoomArgParser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Getter
@Log4j2
public class MainArgs implements Main {

    @Setter
    private ArgParser argParser;

    private final Week week = new Week();
    private final List<Room> rooms = new ArrayList<>();
    private final List<PlannedCourse> courses = new ArrayList<>();
    private final List<Preference> preferences = new ArrayList<>();

    private final TimeTableGenerator generator = new TimeTableGenerator();


    public static void main(String[] args) {
        MainArgs mainArgs = new MainArgs();
        mainArgs.setArgParser(new RoomArgParser(mainArgs));
        mainArgs.run(args);

        System.out.println(mainArgs.getResult());
    }

    @Override
    public OutputDTO generateResult() {
        if( Set.of(rooms, courses, preferences).stream().anyMatch(Collection::isEmpty) || week.getDays().isEmpty() ) {
            throw new IllegalStateException("Main not run before trying to get results.");
        }
        return generator.generate(
                InputDTO.builder()
                .week(week)
                .rooms(rooms)
                .plannedCourses(courses)
                .preferences(preferences)
                .build());
    }

    @Override
    public void log(String logMessage) {
        log.info(logMessage);
    }
}
