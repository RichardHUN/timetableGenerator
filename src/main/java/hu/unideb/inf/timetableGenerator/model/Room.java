package hu.unideb.inf.timetableGenerator.model;

import lombok.*;

import java.util.Objects;

@Data
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private String roomNumber;
    private int capacity;
    /**
     * Each linked list represents a continuous available time window.
     * If there is a break in the availability, a new linked list is started.
     * For example, if the day starts at 8:00, ends at 20:00, and
     * there is a break from 12:00 to 13:00, the availableWindows
     * would contain two linked lists:
     * - First linked list: 8:00, 9:00, 10:00, 11:00
     * - Second linked list: 13:00, 14:00, 15:00, ..., 20:00
     */
    private Week week;

    /**
     * Constructs a basic {@link Room} with a basic {@link Week}.
     * @see Week#Week()
     * @param roomNumber the room number of the created room
     * @param capacity the capacity of the created room
     */
    public Room(String roomNumber, int capacity) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.week = new Week();
    }

    public void occupy(Day day, Time startTime, Time endTime) {
        week.occupyTime(day, startTime, endTime);
    }

    public Room clone() {
        return new Room(this.roomNumber, this.capacity, this.week.clone());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(roomNumber, room.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber);
    }

}
