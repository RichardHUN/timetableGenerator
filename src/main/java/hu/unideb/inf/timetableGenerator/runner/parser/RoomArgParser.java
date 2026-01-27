package hu.unideb.inf.timetableGenerator.runner.parser;

import hu.unideb.inf.timetableGenerator.model.Day;
import hu.unideb.inf.timetableGenerator.model.Room;
import hu.unideb.inf.timetableGenerator.model.Time;
import hu.unideb.inf.timetableGenerator.model.Week;
import hu.unideb.inf.timetableGenerator.runner.Main;

import java.util.*;

public class RoomArgParser implements ArgParser {
    private final Main main;

    public RoomArgParser(Main main) {
        this.main = main;
        nextArgParserClass = new CourseArgParser(main);
    }

    private final ArgParser nextArgParserClass;
    private final WeekReader weekReader = new WeekReader();

    private String roomNumber = "N/A";
    private int capacity;
    private Optional<Week> week;

    private int phase = 0;

    @Override
    public void parse(String arg) {

        switch (phase) {
            case 0 :
                if (arg.equals("-")) {

                    main.setArgParser(nextArgParserClass);
                    return;
                }
                roomNumber = arg;
                phase++;
                break;
            case 1 :
                capacity = main.tryParsingInt(arg, "Cannot parse room capacity: " + arg);
                phase++;
                break;
            case 2 :
                week = weekReader.parse(arg);
                if(week.isEmpty()){
                    break;
                }
                main.getRooms().add(new Room(roomNumber, capacity, week.get()));

                List<Day> combinedDays = combineDays(main.getRooms());
                main.getWeek().setDays(combinedDays);

                phase = 0;
                break;
        }
    }

    private static class WeekReader {
        String dayName;
        List<Day> days = new ArrayList<>();
        List<LinkedList<Time>> availableWindows = new ArrayList<>();
        LinkedList<Time> currentWindow = new LinkedList<>();

        int parseStep = 0;

        /*
         * - <- end of reading days
         * | <- end of reading a day
         * # <- end of reading a continuous time window
         */

        // Monday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 | Tuesday 8:00 9:00 10:00 11:00 # 13:00 14:00 15:00 16:00 17:00 18:00 19:00 -
        public Optional<Week> parse(String arg) {
            if (arg.equals("-")) {
                availableWindows.add(currentWindow);
                days.add(new Day(dayName, availableWindows));
                currentWindow = new LinkedList<>();
                availableWindows = new ArrayList<>();
                parseStep = 0;
                List<Day> resultDays = new ArrayList<>(days);
                days = new ArrayList<>();
                return Optional.of(new Week(resultDays));
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
            return Optional.empty();
        }
    }

    /**
     * Performs logical OR operation on the available times of all rooms,
     * combining their available time windows for each day.
     * @param rooms the rooms of which the available time windows should be combined
     * @return every {@link Time} that appears in at least one {@link Room Room}'s available time windows
     */
    private List<Day> combineDays(List<Room> rooms){
        Map<String, TreeSet<Time>> dayTimes = new LinkedHashMap<>();

        for (Room room : rooms) {
            Week roomWeek = room.getWeek();
            if (roomWeek == null || roomWeek.getDays() == null) {
                continue;
            }

            for (Day day : roomWeek.getDays()) {
                TreeSet<Time> times = dayTimes.computeIfAbsent(
                        day.getName(),
                        k -> new TreeSet<>(Time.getComparator())
                );
                if (day.getAvailableWindows() == null) {
                    continue;
                }
                for (List<Time> window : day.getAvailableWindows()) {
                    times.addAll(window);
                }
            }
        }

        List<Day> combinedDays = new ArrayList<>();
        for (Map.Entry<String, TreeSet<Time>> entry : dayTimes.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }
            List<LinkedList<Time>> combinedWindows = new ArrayList<>();
            LinkedList<Time> currentWindow = new LinkedList<>();
            Time previous = null;

            for (Time time : entry.getValue()) {
                if (previous == null) {
                    currentWindow.add(time);
                    previous = time;
                    continue;
                }

                if (time.equals(previous.plus(1, 0))) {
                    currentWindow.add(time);
                } else {
                    if (!currentWindow.isEmpty()) {
                        combinedWindows.add(currentWindow);
                    }
                    currentWindow = new LinkedList<>();
                    currentWindow.add(time);
                }
                previous = time;
            }

            if (!currentWindow.isEmpty()) {
                combinedWindows.add(currentWindow);
            }

            combinedDays.add(new Day(entry.getKey(), combinedWindows));
        }

        return combinedDays;
    }

}
