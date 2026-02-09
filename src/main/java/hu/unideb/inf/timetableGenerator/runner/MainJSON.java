package hu.unideb.inf.timetableGenerator.runner;

import hu.unideb.inf.timetableGenerator.generator.TimeTableGenerator;
import hu.unideb.inf.timetableGenerator.model.*;
import hu.unideb.inf.timetableGenerator.runner.parser.ArgParser;
import lombok.Data;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Loads {@code input.json} from the resources folder, reads the
 * {@link InputDTO} from it, and generates the timetable.
 */
@Log4j2
@Data
public class MainJSON implements Main {

    private static final String FILE_NAME = "input.json";

    @Setter
    private ArgParser argParser;

    private Week week = new Week();
    private List<Room> rooms = List.of();
    private List<PlannedCourse> courses = List.of();
    private List<Preference> preferences = List.of();

    private final TimeTableGenerator generator = new TimeTableGenerator();

    private InputDTO inputDTO;

    public static void main(String[] args) {
        MainJSON mainJSON = new MainJSON();
        try {
            mainJSON.setup();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to load resource file", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource file", e);
        }

        System.out.println(mainJSON.getResult());
    }

    private void setup() throws URISyntaxException, IOException {
        String resourceFileName = FILE_NAME;
        ClassLoader classLoader = MainJSON.class.getClassLoader();
        URL resourceUrl = classLoader.getResource(resourceFileName);

        if (resourceUrl == null) {
            throw new RuntimeException("Resource file not found: " + resourceFileName);
        }

        File file = new File(resourceUrl.toURI());

        String content = Files.readString(file.toPath());
        ObjectMapper mapper = new ObjectMapper();

        inputDTO = mapper.readValue(content, InputDTO.class);
        this.setWeek(inputDTO.getWeek());
        this.setRooms(inputDTO.getRooms());
        this.setCourses(inputDTO.getPlannedCourses());
        this.setPreferences(inputDTO.getPreferences());

        log("Setup ran successfully.");
    }

    @Override
    public OutputDTO generateResult() {
        if (inputDTO == null) {
            throw new IllegalStateException("Setup not run before trying to get results.");
        }
        log("InputDTO correct. Generating result.");
        return generator.generate(inputDTO);
    }

    @Override
    public void run(String[] args) {
    }

    @Override
    public void log(String logMessage) {
        log.info(logMessage);
    }
}
