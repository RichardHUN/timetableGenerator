package hu.unideb.inf.timetableGenerator.runner;

import hu.unideb.inf.timetableGenerator.generator.TimeTableGenerator;
import hu.unideb.inf.timetableGenerator.model.*;
import hu.unideb.inf.timetableGenerator.runner.parser.ArgParser;
import hu.unideb.inf.timetableGenerator.runner.parser.RoomArgParser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;

@Getter
@Log4j2
public class MainFile implements Main {
    private static final String FILE_NAME = "input.txt";

    @Setter
    private ArgParser argParser;

    private final Week week = new Week();
    private final List<Room> rooms = new ArrayList<>();
    private final List<PlannedCourse> courses = new ArrayList<>();
    private final List<Preference> preferences = new ArrayList<>();

    private final TimeTableGenerator generator = new TimeTableGenerator();

    public static void main(String[] args) {
        MainFile mainFile;
        try {
            Result result = setup();

            mainFile = result.mainFile();
            mainFile.setArgParser(new RoomArgParser(mainFile));
            mainFile.run(result.fileArgs());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to load resource file", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource file", e);
        }

        System.out.println(mainFile.getResult());
    }

    private static Result setup() throws URISyntaxException, IOException {
        String resourceFileName = FILE_NAME;
        ClassLoader classLoader = MainFile.class.getClassLoader();
        URL resourceUrl = classLoader.getResource(resourceFileName);

        if (resourceUrl == null) {
            throw new RuntimeException("Resource file not found: " + resourceFileName);
        }

        File file = new File(resourceUrl.toURI());
        MainFile mainFile = new MainFile();

        String fileContents = Files.readString(file.toPath());
        String[] fileArgs = Arrays.stream(
                fileContents
                .split(System.lineSeparator()))
                .map(str -> str.replace("\"",""))
                .toArray(String[]::new);
        return new Result(mainFile, fileArgs);
    }

    private record Result(MainFile mainFile, String[] fileArgs){}

    @Override
    public OutputDTO generateResult() {
        if(rooms.isEmpty() || courses.isEmpty() || week.getDays().isEmpty() || preferences.isEmpty()) {
            throw new IllegalStateException("Main not run before trying to get results.");
        }
        return generator.generate(InputDTO.builder()
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
