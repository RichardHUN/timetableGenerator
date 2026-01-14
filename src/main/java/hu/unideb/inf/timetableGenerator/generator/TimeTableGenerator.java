package hu.unideb.inf.timetableGenerator.generator;

import hu.unideb.inf.timetableGenerator.model.*;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TimeTableGenerator {

    private final List<Course> courses = new ArrayList<>();

    public OutputDTO generate(@NonNull InputDTO input) {

        final Course.noCollisions collisionsChecker = new Course.noCollisions();

        //TODO: move these into PlannedCourse
        final int COURSE_DURATION_HOURS = 1;
        final int COURSE_DURATION_MINUTES = 40;

        List<PlannedCourse> plannedCourses = input.getPlannedCourses();
        //TODO: implement preferences handling
        List<Preference> preferences = input.getPreferences();
        List<Room> rooms = input.getRooms();
        Week week = input.getWeek();

        PlannedCourse lastPlannedCourse = null;
        Course course;
        boolean created;
        for( PlannedCourse plannedCourse: plannedCourses ) {
            lastPlannedCourse = plannedCourse;
            for( Room room: rooms ) {

                if( plannedCourse.numberOfListeners() > room.capacity() ) {
                    continue;
                }

                for( Day day: week.getDays() ) {
                    for( List<Time> timeWindow: day.getAvailableWindows() ) {
                        for( Time startTime: timeWindow ) {

                            if( !collisionsChecker.test(courses) ) continue;

                            Time endTime = startTime.plus(COURSE_DURATION_HOURS, COURSE_DURATION_MINUTES);
                            course = Course.builder()
                                    .day(day)
                                    .startTime(startTime)
                                    .endTime(endTime)
                                    .presenterName(plannedCourse.presenterName())
                                    .name(plannedCourse.name())
                                    .room(room)
                                    .numberOfListeners(plannedCourse.numberOfListeners())
                                    .build();

                            courses.add(course);
                            return generate(
                                    modifiedInputDTO(input, plannedCourses, plannedCourse, week, day, timeWindow, startTime));
                        }
                    }
                }
            }
        }

        throw new IllegalStateException("Couldn't put " + lastPlannedCourse + " into timetable.");

    }

    private InputDTO modifiedInputDTO(InputDTO input, List<PlannedCourse> plannedCourses, PlannedCourse plannedCourse,
                                             Week week, Day day, List<Time> timeWindow, Time startTime) {
        plannedCourses.remove(plannedCourse);
        timeWindow.remove(startTime);
        if( timeWindow.isEmpty() ) {
            day.getAvailableWindows().remove(timeWindow);
        }
        week = replaceDayWithSameNameInWeek(day, week);
        return input.toBuilder()
                .plannedCourses(plannedCourses)
                .week(week)
                .build();
    }

    private Week replaceDayWithSameNameInWeek(Day day, Week week) {
        for (int i = 0; i < week.getDays().size(); i++) {
            if(week.getDays().get(i).getName().equals(day.getName())) {
                week.getDays().set(i, day);
            }
        }
        return week;
    }

}
