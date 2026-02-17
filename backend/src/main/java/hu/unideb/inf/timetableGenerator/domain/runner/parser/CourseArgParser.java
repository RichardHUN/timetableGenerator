package hu.unideb.inf.timetableGenerator.domain.runner.parser;

import hu.unideb.inf.timetableGenerator.domain.model.PlannedCourse;
import hu.unideb.inf.timetableGenerator.domain.runner.Main;

public class CourseArgParser implements ArgParser {
    private final Main main;

    public CourseArgParser(Main main) {
        this.main = main;
        nextArgParser = new ConstraintParser(main);
    }

    private final ArgParser nextArgParser;

    private String courseName;
    private String presenterName;
    private int numberOfListeners;
    private int durationsHours;
    private int durationMinutes;
    private int parseStep = 0;

    @Override
    public void parse(String arg) {
        if (arg.equals("-")) {
            main.setArgParser(nextArgParser);
            return;
        }

        switch (parseStep) {
            case 0 -> {
                courseName = arg;
                parseStep++;
            }
            case 1 -> {
                presenterName = arg;
                parseStep++;
            }
            case 2 -> {
                numberOfListeners = main.tryParsingInt(arg, "Cannot parse number of listeners: " + arg);
                parseStep++;
            }
            case 3 -> {
                durationsHours = main.tryParsingInt(arg.split(":")[0], "Cannot parse duration hours: " + arg);
                durationMinutes = main.tryParsingInt(arg.split(":")[1], "Cannot parse duration minutes: " + arg);
                main.getCourses().add(new PlannedCourse(courseName, presenterName, numberOfListeners, durationsHours, durationMinutes));
                parseStep = 0;
            }
        }

    }

}