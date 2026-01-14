package hu.unideb.inf.timetableGenerator.model;

import java.util.Objects;

public record Room (
        String roomNumber,
        int capacity
){
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
