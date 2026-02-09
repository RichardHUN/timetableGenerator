package hu.unideb.inf.timetableGenerator.domain.model;

import javafx.util.Pair;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.function.Predicate;

@Getter
@Builder
public class Course {

    private final Day day;
    private final Time startTime;
    private final Time endTime;
    private final String name;
    private final Room room;
    private final String presenterName;
    private final int numberOfListeners;

    public Course(Day day, Time startTime, Time endTime, String name, Room room, String presenterName, int numberOfListeners) {
        if ( numberOfListeners > room.getCapacity() )
            throw new IllegalArgumentException("Number of listeners cannot be more than room capacity.");
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.room = room;
        this.presenterName = presenterName;
        this.numberOfListeners = numberOfListeners;
    }

    /**
     * Checks that there are no collisions between courses.
     * Should be called with all courses in a given day.
     * Calls {@link noCollision} on every possible pair of courses.
     */
    public static class noCollisions implements Predicate<List<Course>> {
        @Override
        public boolean test(List<Course> courses) {
            Predicate<Pair<Course, Course>> collisionChecker = new noCollision();
            Pair<Course, Course> coursePair;
            for (int i = 0; i < courses.size()-1; i++) {
                for (int j = i+1; j < courses.size(); j++) {
                    coursePair = new Pair<>(courses.get(i), courses.get(j));
                    if (!collisionChecker.test(coursePair)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private static class noCollision implements Predicate<Pair<Course, Course>> {
        @Override
        public boolean test(Pair<Course, Course> coursePair) {
            Predicate<Pair<Course, Course>> roomCollisionChecker = new noRoomCollision();
            Predicate<Pair<Course, Course>> presenterCollisionChecker = new noPresenterCollision();

            return roomCollisionChecker.test(coursePair) &&
                    presenterCollisionChecker.test(coursePair);
        }
    }

    private static class noRoomCollision implements Predicate<Pair<Course, Course>> {
        @Override
        public boolean test(Pair<Course, Course> coursePair) {
            Course first = coursePair.getKey();
            Course second = coursePair.getValue();

            if (!first.getRoom().equals(second.getRoom()) || !first.getDay().equals(second.getDay())) {
                return true;
            }

            if (first.startTime.equals(second.startTime)) {
                return false;
            }

            return first.isBefore(second) || second.isBefore(first);
        }
    }

    private static class noPresenterCollision implements Predicate<Pair<Course, Course>> {
        @Override
        public boolean test(Pair<Course, Course> coursePair) {
            Course first = coursePair.getKey();
            Course second = coursePair.getValue();

            if (!first.getPresenterName().equals(second.getPresenterName()) || !first.getDay().equals(second.getDay())) {
                return true;
            }

            if (first.startTime.equals(second.startTime)) {
                return false;
            }

            return first.isBefore(second) || second.isBefore(first);
        }
    }

    private boolean startsBefore(Course other) {
        return Time.getComparator().compare(this.startTime, other.startTime) < 0;
    }

    private boolean endsBeforeStartOf(Course other) {
        return Time.getComparator().compare(this.endTime, other.startTime) <= 0;
    }

    private boolean isBefore(Course other) {
        return this.startsBefore(other) && this.endsBeforeStartOf(other);
    }

    @Override
    public String toString() {
        return "Course{" +
                "day=" + day.getName() +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", name='" + name + '\'' +
                ", room=" + room +
                ", presenterName='" + presenterName + '\'' +
                ", numberOfListeners=" + numberOfListeners +
                '}';
    }
}
