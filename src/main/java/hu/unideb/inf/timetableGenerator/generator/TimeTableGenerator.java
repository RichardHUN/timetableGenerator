package hu.unideb.inf.timetableGenerator.generator;

import hu.unideb.inf.timetableGenerator.model.*;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;
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
        for( PlannedCourse plannedCourse: plannedCourses ) {
            lastPlannedCourse = plannedCourse;
            for( Day day: week.getDays() ) {
                for( List<Time> timeWindow: day.getAvailableWindows() ) {
                    for( Time startTime: timeWindow ) {
                        for( Room room: rooms ) {
                            //System.out.println(room);
                            Room oldRoom = room.clone();

                            if( plannedCourse.numberOfListeners() > room.getCapacity() ) {
                                continue;
                            }

                            if( room.getWeek().getDays().stream().noneMatch(d -> d.getName().equals(day.getName())) ){
                                continue;
                            }

                            /*if( !collisionsChecker.test(courses) ) {
                                continue;
                            }*/

                            //VALAMIVEL E FÖLÖTT VAN A BAJ, ELTŰNIK A VÁLTOZTATÁS
                            for( Day roomDay: room.getWeek().getDays() ) {
                                if( !roomDay.getName().equals(day.getName()) ) {
                                    continue;
                                }
                                System.out.println(roomDay);
                                for( List<Time> roomTimeWindow: roomDay.getAvailableWindows() ) {
                                    if( !roomTimeWindow.contains(startTime) ) {
                                        continue;
                                    }

                                    //VALAMIVEL E FÖLÖTT VAN A BAJ, ELTŰNIK A VÁLTOZTATÁS
                                    //System.out.printf("%s is open on %s\n", roomTimeWindow, startTime);

                                    //System.out.println("Found good time: " + startTime);
                                    //System.out.println(roomDay);

                                    Time endTime = startTime.plus(COURSE_DURATION_HOURS, COURSE_DURATION_MINUTES);

                                    //System.out.println("Before: " + room.getWeek());
                                    room.occupy(roomDay, startTime, endTime);
                                    //System.out.println("After(" + startTime + "-" + endTime + "): " + room.getWeek());

                                    //TODO: remove empty TimeWindows in Day objects
                                    course = Course.builder()
                                            .day(roomDay)
                                            .startTime(startTime)
                                            .endTime(endTime)
                                            .presenterName(plannedCourse.presenterName())
                                            .name(plannedCourse.name())
                                            .room(room)
                                            .numberOfListeners(plannedCourse.numberOfListeners())
                                            .build();

                                    courses.add(course);
                                    plannedCourses.remove(plannedCourse);
                                    rooms.set(rooms.indexOf(oldRoom), room);
                                    //System.out.println(rooms);
                                    //week.setDays(removeAffectedTimeWindows(week, day, startTime, endTime));

                                    input = input.toBuilder()
                                            .plannedCourses(plannedCourses)
                                            .rooms(rooms)
                                            .build();

                                    if(plannedCourses.isEmpty()) {
                                        return new OutputDTO(week, courses);
                                    }

                                    return generate(
                                            input
                                    );

                                }
                            }
                        }
                    }
                }
            }
        }

        throw new IllegalStateException("Couldn't put " + lastPlannedCourse + " into timetable.");
    }

    /*private List<Day> removeAffectedTimeWindows(Week week, Day day, Time startTime, Time endTime) {
        List<Day> updatedDays = new ArrayList<>();
        for (int i = 0; i < week.getDays().size(); i++) {
            if (week.getDays().get(i).equals(day)) {
                List<LinkedList<Time>> availableWindows = day.getAvailableWindows();
                List<LinkedList<Time>> updatedWindows = new ArrayList<>();

                for (LinkedList<Time> window: availableWindows) {
                    LinkedList<Time> updatedWindow = new LinkedList<>();
                    for (Time time: window) {
                        if (time.isEarlierThan(startTime) || time.isLaterThan(endTime)) {
                            updatedWindow.add(time);
                        }
                    }
                    if (!updatedWindow.isEmpty()) {
                        updatedWindows.add(updatedWindow);
                    }
                }
                Day updatedDay = day.toBuilder()
                        .availableWindows(updatedWindows)
                        .build();
                updatedDays.add(updatedDay);
                continue;
            }
            updatedDays.add(week.getDays().get(i));
        }
        return updatedDays;
    }*/

}
