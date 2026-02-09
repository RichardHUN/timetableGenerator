package hu.unideb.inf.timetableGenerator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

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
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Preference implements Predicate<List<Course>> {

    private Predicate<List<Course>> constraint;
    private Integer strictness;
    private String toString;
    private String presenterName;
    private String constraintName;
    private String[] params;

    @Override
    public boolean test(List<Course> courses) {
        return constraint.test(courses);
    }

    @JsonValue
    public String toJsonString() {
        return strictness + ":" + presenterName + ":" + constraintName + ":" + String.join(":", params);
    }

    @JsonCreator
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

        return new Preference(constr, strictness, toString, presenterName, constraintName, remaining);
    }

    private static boolean isValidConstraintName(String string) {
        return ConstraintTypes.isValidConstraintName(string);
    }

    private static String getToString(int strictness, String presenterName, String constraintName, ConstraintTypes.ConstraintType constraintType, String[] remaining) {
        return "[" +
                "Presenter: " +
                presenterName +
                ", Preference: " +
                constraintName +
                ", " +
                ConstraintTypes.getParamStringValue(constraintType) +
                ": " +
                remaining[0] +
                (remaining.length == 2 ? ":" + remaining[1] : "") +
                ", Strictness: " +
                strictness +
                "]";
    }

    @Override
    public String toString() {
        return toString;
    }
}
