package hu.unideb.inf.timetableGenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class Day {

    private final String name;
    /**
     * Each linked list represents a continuous available time window.
     * If there is a break in the availability, a new linked list is started.
     * For example, if the day starts at 8:00, ends at 20:00, and
     * there is a break from 12:00 to 13:00, the availableWindows
     * would contain two linked lists:
     * - First linked list: 8:00, 9:00, 10:00, 11:00
     * - Second linked list: 13:00, 14:00, 15:00, ..., 20:00
     */
    private List<LinkedList<Time>> availableWindows;

    /**
     * Generates a {@link Day}, that starts at 8:00 and ends at 20:00, with no breaks.
     * @param name the name of the day
     */
    public Day(String name) {
        this.name = name;
        this.availableWindows = List.of(Time.from8To20());
    }

}
