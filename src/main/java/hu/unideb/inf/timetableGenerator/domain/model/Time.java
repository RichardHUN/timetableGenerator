package hu.unideb.inf.timetableGenerator.domain.model;

import lombok.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class Time implements Comparable<Time> {

    private int hour;
    private int minute;

    public boolean isLaterThan(Time time) {
        return this.compareTo(time) > 0;
    }

    public boolean isEarlierThan(Time time) {
        return this.compareTo(time) < 0;
    }

    public Time plus(int hour, int minute) {
        int newHour = this.hour + hour;
        int newMinute = this.minute + minute;
        if (newMinute >= 60) {
            newHour += newMinute / 60;
            newMinute = newMinute % 60;
        }
        return Time.builder()
                .hour(newHour)
                .minute(newMinute)
                .build();
    }

    public static Time of(String hourMinute) {
        String[] parts = null;
        Integer hour = null;
        Integer minute = null;
        Time time = null;
        try {
            parts = hourMinute.split(":");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
            time = of(hour, minute);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't split time string: " + hourMinute + " " + e);
        }
        return time;
    }

    public static Time of(int hour, int minute) {
        return Time.builder()
                .hour(hour)
                .minute(minute)
                .build();
    }

    static LinkedList<Time> from8To20() {
        return fromAToB(8, 20);
    }

    static LinkedList<Time> fromAToB(int startHour, int endHour) {
        LinkedList<Time> times = new LinkedList<>();
        for (int hour = startHour; hour <= endHour; hour++) {
            times.add(Time.builder().hour(hour).minute(0).build());
        }
        return times;
    }

    private static class TimeComparator implements Comparator<Time> {
        @Override
        public int compare(Time t1, Time t2) {
            return t1.hour - t2.hour == 0 ? t1.minute - t2.minute : t1.hour - t2.hour;
        }
    }

    private static final Comparator<Time> TIME_COMPARATOR = new TimeComparator();

    public static Comparator<Time> getComparator() {
        return TIME_COMPARATOR;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hour, minute);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Time time = (Time) o;
        return hour == time.hour && minute == time.minute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, minute);
    }

    @Override
    public int compareTo(Time t) {
        return this.hour - t.hour == 0 ? this.minute - t.minute : this.hour - t.hour;
    }
}
