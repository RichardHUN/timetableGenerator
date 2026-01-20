package hu.unideb.inf.timetableGenerator.runner;

import hu.unideb.inf.timetableGenerator.generator.TimeTableGenerator;
import hu.unideb.inf.timetableGenerator.model.*;

import java.util.*;

public class MainArgs {

    private ArgParser argParser = new WeekArgParser();

    private Week week = new Week();
    private final List<Room> rooms = new ArrayList<>();
    private final List<PlannedCourse> courses = new ArrayList<>();
    private final List<Preference> preferences = new ArrayList<>();

    /*
     * Example input:
     * Monday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 | Tuesday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 - 1 200 2 180 3 28 4 32 - Statistics "John Doe" 180 Math "Jane Doe" 24 - "5:John Doe:noClassesAfter:15:00" "3:Jane Doe:noClassesOnDay:Monday" "2:Jane Doe:noClassesBefore:10:00" "1:John Doe:maxClassesPerDay:4"
     *
     * Output:
     * [Room[roomNumber=1, capacity=200], Room[roomNumber=2, capacity=180], Room[roomNumber=3, capacity=28], Room[roomNumber=4, capacity=32]]
     * [PlannedCourse[name=Statistics, presenterName=John Doe, numberOfListeners=180], PlannedCourse[name=Math, presenterName=Jane Doe, numberOfListeners=24]]
     * Week(days=[Day(name=Monday, availableWindows=[[08:00, 09:00, 10:00, 11:00], [13:00, 14:00, 15:00, 16:00, 17:00, 18:00, 19:00]]), Day(name=Tuesday, availableWindows=[[08:00, 09:00, 10:00, 11:00], [13:00, 14:00, 15:00, 16:00, 17:00, 18:00, 19:00]])])
     * [Preference{Presenter: John Doe, Preference: noClassesAfter, Time: 15:00, Strictness: 5}, Preference{Presenter: Jane Doe, Preference: noClassesOnDay, Day: Monday, Strictness: 3}, Preference{Presenter: Jane Doe, Preference: noClassesBefore, Time: 10:00, Strictness: 2}, Preference{Presenter: John Doe, Preference: maxClassesPerDay, Max: 4, Strictness: 1}]
     * */

    /*
    * Rooms - each with a week
    * PlannedCourses
    * Preferences
    * Monday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 |
    * Tuesday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 -
    *
    * 1 200
    * Monday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 |
    * Tuesday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 -
    *
    * 2 180
    * Monday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 |
    * Tuesday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 -
    *
    * 3 28
    * Monday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 |
    * Tuesday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 -
    *
    * 4 32 -
    * Monday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 |
    * Tuesday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 -
    *
    * Statistics "John Doe" 180 Math "Jane Doe" 24 -
    * "5:John Doe:noClassesAfter:15:00"
    * "3:Jane Doe:noClassesOnDay:Monday"
    * "2:Jane Doe:noClassesBefore:10:00"
    * "1:John Doe:maxClassesPerDay:4"
    * */

    public static void main(String[] args) {
        MainArgs mainArgs = new MainArgs();
        mainArgs.run(args);
    }

    private void run(String[] args) {

        for (String arg: args) {
            argParser.parse(arg);
        }

        System.out.println(week);
        System.out.println(rooms);
        System.out.println(courses);
        System.out.println(preferences);

        System.out.println();
        System.out.println();

        System.out.println(new TimeTableGenerator().generate(InputDTO.builder()
                        .week(week)
                        .plannedCourses(courses)
                        .rooms(rooms)
                        .preferences(preferences)
                        .build()));

    }

    private interface ArgParser {
        void parse(String arg);
    }

    private class RoomArgParser implements ArgParser {
        private final ArgParser nextArgParserClass = new CourseArgParser();
        private final WeekReader weekReader = new WeekReader();

        private String roomNumber = "N/A";
        private int capacity;
        private Week week;

        private int phase = 0;

        @Override
        public void parse(String arg) {

            switch (phase) {
                case 0 :
                    if (arg.equals("-")) {
                        argParser = nextArgParserClass;
                        return;
                    }
                    roomNumber = arg;
                    phase++;
                    break;
                case 1 :
                    capacity = tryParsingInt(arg, "Cannot parse room capacity: " + arg);
                    phase++;
                    break;
                case 2 :
                    try {
                        week = weekReader.parse(arg);
                        rooms.add(new Room(roomNumber, capacity, week));
                        phase = 0;
                        break;
                    } catch (IllegalStateException e) {
                        break;
                    }
            }
        }

        private static class WeekReader {
            List<Day> days = new ArrayList<>();
            String dayName;
            List<LinkedList<Time>> availableWindows = new ArrayList<>();
            LinkedList<Time> currentWindow = new LinkedList<>();

            int parseStep = 0;

            /*
             * - <- end of reading days
             * | <- end of reading a day
             * # <- end of reading a continuous time window
             */

            // Monday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 | Tuesday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 -
            public Week parse(String arg) {
                if (arg.equals("-")) {
                    availableWindows.add(currentWindow);
                    currentWindow = new LinkedList<>();
                    availableWindows = new ArrayList<>();
                    parseStep = 0;
                    days.add(new Day(dayName, availableWindows));
                    return new Week(days);
                }

                switch (parseStep) {
                    case 0:
                        dayName = arg;
                        parseStep++;
                        break;
                    case 1:
                        if (arg.equals("#")) {
                            availableWindows.add(currentWindow);
                            currentWindow = new LinkedList<>();
                            break;
                        }
                        if (arg.equals("|")) {
                            availableWindows.add(currentWindow);
                            currentWindow = new LinkedList<>();
                            days.add(new Day(dayName, availableWindows));
                            availableWindows = new ArrayList<>();
                            parseStep = 0;
                            break;
                        }
                        Time time = Time.of(arg);
                        currentWindow.add(time);
                        break;
                }
                throw new IllegalStateException("Unexpected state in WeekReader");
            }
        }

    }

    private class WeekArgParser implements ArgParser {
        private final ArgParser nextArgParser = new RoomArgParser();

        List<Day> days = new ArrayList<>();
        String dayName;
        List<LinkedList<Time>> availableWindows = new ArrayList<>();
        LinkedList<Time> currentWindow = new LinkedList<>();

        int parseStep = 0;

        /*
         * - <- end of reading days
         * | <- end of reading a day
         * # <- end of reading a continuous time window
         */

        // Monday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 | Tuesday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 -
        @Override
        public void parse(String arg) {
            if (arg.equals("-")) {
                availableWindows.add(currentWindow);
                days.add(new Day(dayName, availableWindows));
                week = new Week(days);
                argParser = nextArgParser;
                return;
            }

            switch (parseStep) {
                case 0:
                    dayName = arg;
                    parseStep++;
                    break;
                case 1:
                    if (arg.equals("#")) {
                        availableWindows.add(currentWindow);
                        currentWindow = new LinkedList<>();
                        break;
                    }
                    if (arg.equals("|")) {
                        availableWindows.add(currentWindow);
                        currentWindow = new LinkedList<>();
                        days.add(new Day(dayName, availableWindows));
                        availableWindows = new ArrayList<>();
                        parseStep = 0;
                        return;
                    }
                    Time time = Time.of(arg);
                    currentWindow.add(time);
                    break;
            }
        }
    }

    private class CourseArgParser implements ArgParser {
        String courseName;
        String presenterName;
        int numberOfListeners;
        int parseStep = 0;

        @Override
        public void parse(String arg) {
            if (arg.equals("-")) {
                argParser = new ConstraintParser();
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
                    numberOfListeners = tryParsingInt(arg, "Cannot parse number of listeners: " + arg);
                    courses.add(new PlannedCourse(courseName, presenterName, numberOfListeners));
                    parseStep = 0;
                }
            }

        }

    }

    private class ConstraintParser implements ArgParser {
        /*
         * "5:Jane Doe:noClassesBefore:10:00"
         */
        @Override
        public void parse(String arg) {
            if (arg.equals("-")) {
                //TODO: replace parser
                return;
            }

            Preference preference = Preference.of(arg);
            preferences.add(preference);
        }
    }

    private int tryParsingInt(String number, String message) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException(message);
        }
    }

}
