package hu.unideb.inf.timetableGenerator.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represent a constraint/preference of a presenter.
 * A constraint/preference can be described the following way:
 * {@code <presenterName>:<constraintName>:<param1>}
 * Current possible preferences:
 * <ul>
 *     <li>noClassesOnDay: {@code <dayName>}</li>
 *     <li>maxClassesPerDay: {@code <maxClasses>}</li>
 *     <li>noClassesAfter: {@code <hour>:<minute>}</li>
 *     <li>noClassesBefore: {@code <hour>:<minute>}</li>
 * </ul>
 * Example:
 * <ul>
 *     <li>{@code "James Doe:noClassesAfter:15:00"}</li>
 *     <li>{@code "Jane Doe:noClassesOnDay:Friday"}</li>
 * </ul>
 */
public class Preference implements Predicate<List<Course>> {

    private final Predicate<List<Course>> constraint;
    @Getter
    private final Integer strictness;
    private String toString;

    @Override
    public boolean test(List<Course> courses) {
        return constraint.test(courses);
    }

    private static final class ConstraintTypes {
        private static final String NO_CLASSES_ON_DAY = "noClassesOnDay";
        private static final String MAX_CLASSES_PER_DAY = "maxClassesPerDay";
        private static final String NO_CLASSES_AFTER = "noClassesAfter";
        private static final String NO_CLASSES_BEFORE = "noClassesBefore";

        public enum ConstraintType {
            NO_CLASSES_ON_DAY,
            MAX_CLASSES_PER_DAY,
            NO_CLASSES_AFTER,
            NO_CLASSES_BEFORE
        }

        public static boolean isValidConstraintName(String string) {
            return switch (string) {
                case NO_CLASSES_ON_DAY,
                     MAX_CLASSES_PER_DAY,
                     NO_CLASSES_AFTER,
                     NO_CLASSES_BEFORE -> true;
                default -> false;
            };
        }

        public static ConstraintType of(String string) {
            return switch (string) {
                case NO_CLASSES_ON_DAY -> ConstraintType.NO_CLASSES_ON_DAY;
                case MAX_CLASSES_PER_DAY -> ConstraintType.MAX_CLASSES_PER_DAY;
                case NO_CLASSES_AFTER -> ConstraintType.NO_CLASSES_AFTER;
                case NO_CLASSES_BEFORE -> ConstraintType.NO_CLASSES_BEFORE;
                default -> throw new IllegalArgumentException("Unknown constraint: " + string);
            };
        }

        public static String getParamStringValue(ConstraintType constraintType) {
            return switch (constraintType) {
                case NO_CLASSES_ON_DAY -> "Day";
                case MAX_CLASSES_PER_DAY -> "Max";
                case NO_CLASSES_AFTER, NO_CLASSES_BEFORE -> "Time";
            };
        }

        public static Predicate<List<Course>> getPredicate(ConstraintType constraintType,
                                                           String presenterName,
                                                           String[] remainingParams) {
            return switch (constraintType) {
                case NO_CLASSES_ON_DAY -> {
                    String dayName = remainingParams[0];
                    yield courses -> courses.stream()
                            .filter(course -> course.getPresenterName().equals(presenterName))
                            .noneMatch(course -> course.getDay().getName().equals(dayName));
                }
                case MAX_CLASSES_PER_DAY -> {
                    int maxClasses = Integer.parseInt(remainingParams[0]);
                    yield courses -> courses.stream()
                            .filter(course -> course.getPresenterName().equals(presenterName))
                            .collect(Collectors.groupingBy(course -> course.getDay().getName()))
                            .values().stream()
                            .noneMatch(list -> list.size() > maxClasses);
                }
                case NO_CLASSES_AFTER -> {
                    Time timeAfter = Time.of(remainingParams[0] + ":" + remainingParams[1]);
                    yield courses -> courses.stream()
                            .filter(course -> course.getPresenterName().equals(presenterName))
                            .allMatch(course -> course.getEndTime().compareTo(timeAfter) <= 0);
                }
                case NO_CLASSES_BEFORE -> {
                    Time timeBefore = Time.of(remainingParams[0] + ":" + remainingParams[1]);
                    yield courses -> courses.stream()
                            .filter(course -> course.getPresenterName().equals(presenterName))
                            .allMatch(course -> course.getStartTime().compareTo(timeBefore) >= 0);
                }
            };
        }
    }

    public static Preference of(String string) {
        Predicate<List<Course>> constr;

        String[] split = string.split(":");
        int strictness = Integer.parseInt(split[0]);
        String presenterName = split[1];
        String constraintName = split[2];

        if (!isValidConstraintName(constraintName)) {
            throw new IllegalArgumentException("Invalid constraint name: " + constraintName);
        }
        ConstraintTypes.ConstraintType constraintType = ConstraintTypes.of(constraintName);

        String[] remaining = Arrays.stream(split).skip(3).toArray(String[]::new);

        String toString =
                getToString(strictness, presenterName, constraintName, constraintType, remaining);

        constr = ConstraintTypes.getPredicate(constraintType, presenterName, remaining);

        return new Preference(constr, toString, strictness);
    }

    private Preference(Predicate<List<Course>> constraint, int strictness) {
        this.constraint = constraint;
        this.strictness = strictness;
    }

    private Preference(Predicate<List<Course>> constraint, String toString, int strictness) {
        this(constraint, strictness);
        this.toString = toString;
    }

    private static boolean isValidConstraintName(String string) {
        return ConstraintTypes.isValidConstraintName(string);
    }

    private static String getToString(int strictness, String presenterName, String constraintName, ConstraintTypes.ConstraintType constraintType, String[] remaining) {
        return "Presenter: " +
                presenterName +
                ", Preference: " +
                constraintName +
                ", " +
                ConstraintTypes.getParamStringValue(constraintType) +
                ": " +
                remaining[0] +
                (remaining.length == 2 ? ":" + remaining[1] : "") +
                ", Strictness: " +
                strictness;
    }

    @Override
    public String toString() {
        return "Preference{" +
                toString +
                '}';
    }
}
