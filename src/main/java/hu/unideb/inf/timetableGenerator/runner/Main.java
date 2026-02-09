package hu.unideb.inf.timetableGenerator.runner;

import hu.unideb.inf.timetableGenerator.model.*;
import hu.unideb.inf.timetableGenerator.runner.parser.ArgParser;

import java.util.List;

/**
 * Describes a class capable of reading arguments, generating a result, and logging.
 * The {@link #generateResult()} should generate the result and return it.
 * The result should be retrieved by calling the {@link #getResult()} method,
 * which calls the {@link #generateResult()} method and log the success.
 * The {@link #run(String[])} method should be called to start the argument parsing process,
 * which should call the {@link #getArgParser()} method to get the current {@link ArgParser} and pass the arguments to it.
 * The {@link #setArgParser(ArgParser)} method should be called to change the current {@link ArgParser}
 * after the current one read it's last argument.
 */
public interface Main {

    /**
     * Goes through the arguments and passes them one-by-one
     * to the {@link ArgParser}.
     * @param args the arguments to parse
     */
    default void run(String[] args) {
        for (String arg: args) {
            getArgParser().parse(arg);
        }
        log("Parsing arguments complete!");
    }

    /**
     * Generates the result, logs success, and returns it.
     * Uses the {@link #generateResult()} method.
     * @return the generated result
     */
    default OutputDTO getResult() {
        OutputDTO result = generateResult();
        log("getResult completed");
        return result;
    }

    /**
     * Should pass the {@link InputDTO} to the {@link hu.unideb.inf.timetableGenerator.generator.TimeTableGenerator}.
     * Shouldn't be called directly. Use {@link #getResult()} instead.
     * @return the generated result
     */
    OutputDTO generateResult();

    ArgParser getArgParser();
    Week getWeek();
    List<Room> getRooms();
    List<PlannedCourse> getCourses();
    List<Preference> getPreferences();

    /**
     * Should be called after the current {@link ArgParser} read it's last argument.
     * This new {@link ArgParser} will be used for the next argument parsing.
     * @param argParser the new {@link ArgParser}
     */
    void setArgParser(ArgParser argParser);

    /**
     * Tries to parse an integer from a string.
     * @param arg the string to parse
     * @param errorMessage the error message to throw if the parsing fails
     * @return the parsed integer or throws an {@link IllegalArgumentException}
     */
    default int tryParsingInt(String arg, String errorMessage) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    void log(String logMessage);

}
