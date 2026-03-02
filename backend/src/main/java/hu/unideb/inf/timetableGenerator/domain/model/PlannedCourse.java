package hu.unideb.inf.timetableGenerator.domain.model;

/**
 * A planned course, which is a course that is planned to be held in the future.
 * It contains the name of the course, the name of the presenter, the number of listeners, and the duration of the course in hours and minutes.
 * <p></p>
 * Not to be confused with {@link Course}, which is a course that has already been put on a timetable
 * and has a room, and a presenter.
 * @param name the name of the course (e.g.: "Mathematics 101")
 * @param presenterName the name of the presenter (e.g.: "Dr. John Doe")
 * @param numberOfListeners the number of listeners (e.g.: 180)
 * @param durationHours the duration of the course in hours (e.g.: 1)
 * @param durationMinutes the duration of the course in minutes (e.g.: 30) - on top of the hours duration
 */
public record PlannedCourse (
    String name,
    String presenterName,
    Integer numberOfListeners,
    Integer durationHours,
    Integer durationMinutes
){}