package hu.unideb.inf.timetableGenerator.dto;

import hu.unideb.inf.timetableGenerator.domain.model.*;
import lombok.*;

import java.util.*;

/**
 * Simpler version of the {@link InputDTO}, used from frontend.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SimpleInputDTO{

    private List<Room> rooms;
    private List<PlannedCourse> plannedCourses;
    private List<Preference> preferences;

    /**
     * Builds a {@link Week} by performing a logical OR over all rooms' weeks:
     * a {@link Time} slot is included if it appears in at least one room's week.
     * The resulting day order follows the first room's day order.
     */
    public static Week buildCombinedWeek(List<Room> rooms) {
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