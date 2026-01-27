package hu.unideb.inf.timetableGenerator.runner;

import hu.unideb.inf.timetableGenerator.model.*;
import hu.unideb.inf.timetableGenerator.runner.parser.ArgParser;

import java.util.List;

public interface Main {

    default void run(String[] args) {
        for (String arg: args) {
            getArgParser().parse(arg);
        }
        log("Parsing arguments complete!");
    }

    default OutputDTO getResult() {
        OutputDTO result = generateResult();
        log("getResult completed");
        return result;
    }

    OutputDTO generateResult();

    ArgParser getArgParser();
    Week getWeek();
    List<Room> getRooms();
    List<PlannedCourse> getCourses();
    List<Preference> getPreferences();

    void setArgParser(ArgParser argParser);

    default int tryParsingInt(String arg, String errorMessage) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    void log(String logMessage);

}
