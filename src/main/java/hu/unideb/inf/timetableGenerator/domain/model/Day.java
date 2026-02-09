package hu.unideb.inf.timetableGenerator.domain.model;

import lombok.*;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Day implements Cloneable {
    /**
     * Contains the name of the day and the available time windows in that day,
     * represented as a {@link Week} object.
     */

    private String name;
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
        this.availableWindows = new java.util.ArrayList<>();
        this.availableWindows.add(Time.from8To20());
    }

    public void occupyTime(Time startTime, Time endTime) {
        for( Time time = startTime; time.isEarlierThan(endTime); time = time.plus(1, 0) ) {
            for( int i = 0; i < availableWindows.size(); i++ ) {
                LinkedList<Time> timeWindow = availableWindows.get(i);
                if( timeWindow.contains(time) ) {
                    timeWindow.remove(time);
                    availableWindows.set(i, timeWindow);
                    break;
                }
            }
        }
    }

    @Override
    public Day clone() {
        try {
            Day clone = (Day) super.clone();
            clone.availableWindows = this.availableWindows.stream()
                    .map(LinkedList::new)
                    .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
