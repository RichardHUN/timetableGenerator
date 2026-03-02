package hu.unideb.inf.timetableGenerator.domain.runner.parser;

/**
 * Represents a command line argument parser. <br>
 * Each implementation of this interface should define how to parse a specific type of argument. <br>
 * Each implementation of this interface should have a {@link hu.unideb.inf.timetableGenerator.domain.runner.Main Main}
 * instance, which's setter is used in the {@link #parse(String)} method.
 */
public interface ArgParser {

    /**
     * Should
     * @param arg the current token(word, string between two spaces)
     */
    void parse(String arg);
}