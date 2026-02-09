package hu.unideb.inf.timetableGenerator.domain.runner;

import hu.unideb.inf.timetableGenerator.domain.generator.TimeTableGenerator;
import hu.unideb.inf.timetableGenerator.domain.model.*;
import hu.unideb.inf.timetableGenerator.domain.runner.parser.ArgParser;
import hu.unideb.inf.timetableGenerator.domain.runner.parser.RoomArgParser;
import hu.unideb.inf.timetableGenerator.dto.InputDTO;
import hu.unideb.inf.timetableGenerator.dto.OutputDTO;
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

    /*
    Args example:
    1 200 Monday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 | Tuesday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 - 2 180 Monday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 | Tuesday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 - 3 28 Monday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 | Tuesday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 - 4 32 Monday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 | Tuesday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 - - Statistics "John Doe" 180 Math "Jane Doe" 24 - "5:John Doe:noClassesAfter:15:00" "3:Jane Doe:noClassesOnDay:Monday" "2:Jane Doe:noClassesBefore:10:00" "1:John Doe:maxClassesPerDay:4"
     */
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
