package hu.unideb.inf.timetableGenerator.model;

import lombok.ToString;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

final class ConstraintTypes {
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
