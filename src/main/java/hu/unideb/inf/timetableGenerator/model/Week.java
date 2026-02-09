package hu.unideb.inf.timetableGenerator.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@ToString
@Builder(toBuilder = true)
public class Week implements Cloneable {

    private List<Day> days;

    /**
     * Constructs a basic {@link Week}, with 5 {@link Day days} (Monday to Friday),
     * each starting at 8:00 and ending at 20:00, with no breaks.
     * Uses the {@link Day#Day(String)} constructor.
     */
    public Week() {
        List<Day> days = new ArrayList<>();
        days.add(new Day("Monday"));
        days.add(new Day("Tuesday"));
        days.add(new Day("Wednesday"));
        days.add(new Day("Thursday"));
        days.add(new Day("Friday"));
        this.days = days;
    }

    public void occupyTime(Day day, Time startTime, Time endTime) {
        if( days.stream().noneMatch(d -> d.getName().equals(day.getName())) ) {
            throw new IllegalArgumentException("There is no day in the given week called " + day.getName());
        }
        for (Day d : days) {
            if (d.getName().equals(day.getName())) {
                int dayIndex = days.indexOf(d);
                d.occupyTime(startTime, endTime);
                days.set(dayIndex, d);
                return;
            }
        }
    }

    @Override
    public Week clone() {
        try {
            Week clone = (Week) super.clone();
            List<Day> newDays = new ArrayList<>();
            for (Day d : this.days) {
                newDays.add(d.clone());
            }
            clone.setDays(newDays);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
