package hu.unideb.inf.timetableGenerator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@ToString
@Builder(toBuilder = true)
public class Week {

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

}
