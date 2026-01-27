package hu.unideb.inf.timetableGenerator.generator;

import hu.unideb.inf.timetableGenerator.model.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class TimeTableGenerator {

    private static final int MAX_SOLUTIONS = 100;
    private int solutionsFound = 0;

    public OutputDTO generate(@NonNull InputDTO input) {
        log.info("Starting timetable generation with preference-based scoring");

        List<SolutionCandidate> candidates = new ArrayList<>();

        generateCandidates(input, new ArrayList<>(), candidates);

        if (candidates.isEmpty()) {
            log.error("Failed to generate any valid timetable");
            throw new IllegalStateException("Failed to generate timetable");
        }

        SolutionCandidate best = candidates.stream()
            .min(Comparator.comparingInt(SolutionCandidate::score))
            .orElseThrow();

        log.info("Timetable generation successful. Found {} candidates, best score: {}",
                 candidates.size(), best.score());

        List<Course> coursesWithFinalRoomStates = rebuildCoursesWithFinalRoomStates(best.courses(), input.getRooms());

        return new OutputDTO(input.getWeek(), coursesWithFinalRoomStates);
    }

    private void generateCandidates(@NonNull InputDTO input, List<Course> currentCourses, List<SolutionCandidate> candidates) {
        if (solutionsFound >= MAX_SOLUTIONS) {
            return;
        }

        final Course.noCollisions collisionsChecker = new Course.noCollisions();

        Week week = input.getWeek();
        List<Room> rooms = new ArrayList<>(input.getRooms());
        List<PlannedCourse> plannedCourses = new ArrayList<>(input.getPlannedCourses());
        List<Preference> preferences = input.getPreferences();


        if (plannedCourses.isEmpty()) {
            int score = scoreSolution(currentCourses, preferences);
            candidates.add(new SolutionCandidate(new ArrayList<>(currentCourses), score));
            solutionsFound++;
            log.debug("Found solution #{} with score: {}", solutionsFound, score);
            return;
        }


        PlannedCourse plannedCourse = plannedCourses.getFirst();

        for (Day day : week.getDays()) {
            for (List<Time> timeWindow : day.getAvailableWindows()) {
                for (Time startTime : timeWindow) {
                    for (Room room : rooms) {

                        if (plannedCourse.numberOfListeners() > room.getCapacity()) {
                            continue;
                        }

                        if (room.getWeek().getDays().stream().noneMatch(d -> d.getName().equals(day.getName()))) {
                            continue;
                        }

                        for (Day roomDay : room.getWeek().getDays()) {
                            if (!roomDay.getName().equals(day.getName())) {
                                continue;
                            }

                            for (List<Time> roomTimeWindow : roomDay.getAvailableWindows()) {
                                if (!roomTimeWindow.contains(startTime)) {
                                    continue;
                                }

                                Time endTime = startTime.plus(plannedCourse.durationsHours(), plannedCourse.durationMinutes());

                                Room roomCopy = room.clone();
                                roomCopy.occupy(roomDay, startTime, endTime);

                                Day updatedRoomDay = roomCopy.getWeek().getDays().stream()
                                        .filter(d -> d.getName().equals(roomDay.getName()))
                                        .findFirst()
                                        .orElseThrow();

                                Course course = Course.builder()
                                        .day(updatedRoomDay)
                                        .startTime(startTime)
                                        .endTime(endTime)
                                        .presenterName(plannedCourse.presenterName())
                                        .name(plannedCourse.name())
                                        .room(roomCopy)
                                        .numberOfListeners(plannedCourse.numberOfListeners())
                                        .build();

                                List<Course> newCourses = new ArrayList<>(currentCourses);
                                newCourses.add(course);

                                if (!collisionsChecker.test(newCourses)) {
                                    continue;
                                }

                                boolean violatesHighStrictnessPreference = false;
                                for (Preference preference : preferences) {
                                    if (preference.getStrictness() >= 5 && !preference.test(newCourses)) {
                                        violatesHighStrictnessPreference = true;
                                        break;
                                    }
                                }

                                if (violatesHighStrictnessPreference) {
                                    if (solutionsFound >= 10) {
                                        continue;
                                    }
                                }

                                List<PlannedCourse> remainingPlanned = new ArrayList<>(plannedCourses);
                                remainingPlanned.removeFirst();

                                List<Room> updatedRooms = new ArrayList<>(rooms);
                                int roomIndex = updatedRooms.indexOf(room);
                                updatedRooms.set(roomIndex, roomCopy);

                                InputDTO newInput = input.toBuilder()
                                        .plannedCourses(remainingPlanned)
                                        .rooms(updatedRooms)
                                        .build();

                                generateCandidates(newInput, newCourses, candidates);

                                if (solutionsFound >= MAX_SOLUTIONS) {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private int scoreSolution(List<Course> courses, List<Preference> preferences) {
        int score = 0;
        for (Preference preference : preferences) {
            if (!preference.test(courses)) {
                score += preference.getStrictness();
            }
        }
        return score;
    }

    private List<Course> rebuildCoursesWithFinalRoomStates(List<Course> courses, List<Room> initialRooms) {
        List<Room> finalRooms = initialRooms.stream()
                .map(Room::clone)
                .toList();

        for (Course course : courses) {
            Room room = finalRooms.stream()
                    .filter(r -> r.getRoomNumber().equals(course.getRoom().getRoomNumber()))
                    .findFirst()
                    .orElseThrow();

            room.occupy(course.getDay(), course.getStartTime(), course.getEndTime());
        }

        List<Course> rebuiltCourses = new ArrayList<>();
        for (Course course : courses) {
            Room finalRoom = finalRooms.stream()
                    .filter(r -> r.getRoomNumber().equals(course.getRoom().getRoomNumber()))
                    .findFirst()
                    .orElseThrow();

            Day finalDay = finalRoom.getWeek().getDays().stream()
                    .filter(d -> d.getName().equals(course.getDay().getName()))
                    .findFirst()
                    .orElseThrow();

            Course rebuiltCourse = Course.builder()
                    .day(finalDay)
                    .startTime(course.getStartTime())
                    .endTime(course.getEndTime())
                    .presenterName(course.getPresenterName())
                    .name(course.getName())
                    .room(finalRoom)
                    .numberOfListeners(course.getNumberOfListeners())
                    .build();

            rebuiltCourses.add(rebuiltCourse);
        }

        return rebuiltCourses;
    }

    private record SolutionCandidate(List<Course> courses, int score) {
    }
}
