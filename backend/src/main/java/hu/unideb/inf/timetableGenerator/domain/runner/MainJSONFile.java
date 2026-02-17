package hu.unideb.inf.timetableGenerator.domain.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.unideb.inf.timetableGenerator.domain.generator.TimeTableGenerator;
import hu.unideb.inf.timetableGenerator.domain.model.*;
import hu.unideb.inf.timetableGenerator.domain.runner.parser.ArgParser;
import hu.unideb.inf.timetableGenerator.domain.runner.utils.ResourceFileParser;
import hu.unideb.inf.timetableGenerator.dto.InputDTO;
import hu.unideb.inf.timetableGenerator.dto.OutputDTO;
import lombok.Data;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * Loads {@code input.json} from the resources folder, reads the
 * {@link InputDTO} from it, and generates the timetable.
 */
@Log4j2
@Data
public class MainJSONFile implements Main {

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
        MainJSONFile mainJSONFile = new MainJSONFile();
        mainJSONFile.setup();

        System.out.println(mainJSONFile.getResult());
    }

    private void setup(){
        String content = ResourceFileParser.parseResourceFile(FILE_NAME);
        ObjectMapper mapper = new ObjectMapper();

        try {
            inputDTO = mapper.readValue(content, InputDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

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
