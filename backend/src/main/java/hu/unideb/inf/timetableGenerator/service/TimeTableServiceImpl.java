package hu.unideb.inf.timetableGenerator.service;

import hu.unideb.inf.timetableGenerator.domain.generator.TimeTableGenerator;
import hu.unideb.inf.timetableGenerator.domain.model.Day;
import hu.unideb.inf.timetableGenerator.domain.model.Room;
import hu.unideb.inf.timetableGenerator.domain.model.Time;
import hu.unideb.inf.timetableGenerator.domain.model.Week;
import hu.unideb.inf.timetableGenerator.dto.InputDTO;
import hu.unideb.inf.timetableGenerator.dto.OutputDTO;
import hu.unideb.inf.timetableGenerator.dto.SimpleInputDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeTableServiceImpl implements TimeTableService {

    //private final repository...
    private final TimeTableGenerator timeTableGenerator;

    @Override
    public OutputDTO generateTimeTable(@NonNull InputDTO input) {
        return timeTableGenerator.generate(input);
    }

    @Override
    public OutputDTO generateTimeTableFromSimpleInput(@NonNull SimpleInputDTO input) {
        Week week = buildCombinedWeek(input.getRooms());

        InputDTO fullInput = InputDTO.builder()
                .week(week)
                .rooms(input.getRooms())
                .plannedCourses(input.getPlannedCourses())
                .preferences(input.getPreferences())
                .build();

        return timeTableGenerator.generate(fullInput);
    }

    /**
     * Builds a {@link Week} by performing a logical OR over all rooms' weeks:
     * a {@link Time} slot is included if it appears in at least one room's week.
     * The resulting day order follows the first room's day order.
     */
    private Week buildCombinedWeek(List<Room> rooms) {
        // Collect all day names in their original order (preserving first-seen order)
        List<String> orderedDayNames = rooms.stream()
                .flatMap(room -> room.getWeek().getDays().stream())
                .map(Day::getName)
                .distinct()
                .toList();

        List<Day> combinedDays = new ArrayList<>();

        for (String dayName : orderedDayNames) {
            // Union of all Time slots for this day across all rooms
            Set<Time> allTimes = new TreeSet<>();
            for (Room room : rooms) {
                room.getWeek().getDays().stream()
                        .filter(d -> d.getName().equals(dayName))
                        .flatMap(d -> d.getAvailableWindows().stream())
                        .flatMap(Collection::stream)
                        .forEach(allTimes::add);
            }

            // Group consecutive times (1-hour steps) into separate windows
            List<LinkedList<Time>> availableWindows = new ArrayList<>();
            LinkedList<Time> currentWindow = new LinkedList<>();
            Time previous = null;

            for (Time time : allTimes) {
                if (previous != null && !previous.plus(1, 0).equals(time)) {
                    availableWindows.add(currentWindow);
                    currentWindow = new LinkedList<>();
                }
                currentWindow.add(time);
                previous = time;
            }
            if (!currentWindow.isEmpty()) {
                availableWindows.add(currentWindow);
            }

            combinedDays.add(new Day(dayName, availableWindows));
        }

        return new Week(combinedDays);
    }

}
