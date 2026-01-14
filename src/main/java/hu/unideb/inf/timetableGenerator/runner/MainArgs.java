package hu.unideb.inf.timetableGenerator.runner;

import hu.unideb.inf.timetableGenerator.model.*;

import java.util.*;

public class MainArgs {

    private ArgParser argParser = new WeekArgParser();

    private Week week;
    private final List<Room> rooms = new ArrayList<>();
    private final List<PlannedCourse> courses = new ArrayList<>();
    private final List<Preference> preferences = new ArrayList<>();

    /*
     * Example input:
     * Monday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 | Tuesday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 - 1 200 2 180 3 28 4 32 - Statistics "James Doe" 180 Math "Jane Doe" 24 - "5:James Doe:noClassesAfter:15:00" "3:Jane Doe:noClassesOnDay:Monday" "2:Jane Doe:noClassesBefore:10:00" "1:James Doe:maxClassesPerDay:4"
     *
     * Output:
     * [Room[roomNumber=1, capacity=200], Room[roomNumber=2, capacity=180], Room[roomNumber=3, capacity=28], Room[roomNumber=4, capacity=32]]
     * [PlannedCourse[name=Statistics, presenterName=James Doe, numberOfListeners=180], PlannedCourse[name=Math, presenterName=Jane Doe, numberOfListeners=24]]
     * Week(days=[Day(name=Monday, availableWindows=[[08:00, 09:00, 10:00, 11:00], [13:00, 14:00, 15:00, 16:00, 17:00, 18:00, 19:00]]), Day(name=Tuesday, availableWindows=[[08:00, 09:00, 10:00, 11:00], [13:00, 14:00, 15:00, 16:00, 17:00, 18:00, 19:00]])])
     * [Preference{Presenter: James Doe, Preference: noClassesAfter, Time: 15:00, Strictness: 5}, Preference{Presenter: Jane Doe, Preference: noClassesOnDay, Day: Monday, Strictness: 3}, Preference{Presenter: Jane Doe, Preference: noClassesBefore, Time: 10:00, Strictness: 2}, Preference{Presenter: James Doe, Preference: maxClassesPerDay, Max: 4, Strictness: 1}]
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
    }

    private interface ArgParser {
        void parse(String arg);
    }

    private class WeekArgParser implements ArgParser {
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
                argParser = new RoomArgParser();
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

    private class RoomArgParser implements ArgParser {
        private String roomNumber = "N/A";
        private int capacity;
        private boolean roomNumberNext = true;

        @Override
        public void parse(String arg) {
            if (arg.equals("-")) {
                argParser = new CourseArgParser();
                return;
            }

            if (roomNumberNext) {
                roomNumber = arg;
                roomNumberNext = false;
                return;
            }
            capacity = tryParsingInt(arg, "Cannot parse room capacity: " + arg);
            roomNumberNext = true;
            rooms.add(new Room(roomNumber, capacity));
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
