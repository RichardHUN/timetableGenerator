package hu.unideb.inf.timetableGenerator.domain.generator;

import hu.unideb.inf.timetableGenerator.domain.model.Time;
import hu.unideb.inf.timetableGenerator.dto.InputDTO;
import hu.unideb.inf.timetableGenerator.dto.OutputDTO;
import hu.unideb.inf.timetableGenerator.dto.SimpleInputDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import tools.jackson.databind.ObjectMapper;

public class TimeTableGeneratorTests {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Includes:
     *  -one Planned Course:
     *      -Statistics - John Doe - 180 listeners - 1 hour 40 minutes
     *  -one Room:
     *      -IF-01 - 200 capacity:
     *          -Monday:
     *              -Single time window:
     *                  -8:00
     *                  -9:00
     */
    private static final SimpleInputDTO minimalSimpleInput = MAPPER.readValue("{\"rooms\":[{\"roomNumber\":\"IF-01\",\"capacity\":200,\"week\":{\"days\":[{\"name\":\"Monday\",\"availableWindows\":[[{\"hour\":8,\"minute\":0},{\"hour\":9,\"minute\":0}]]}]}}],\"plannedCourses\":[{\"name\":\"Statistics\",\"presenterName\":\"John Doe\",\"numberOfListeners\":180,\"durationHours\":1,\"durationMinutes\":40}],\"preferences\":[]}",
            SimpleInputDTO.class);
    private static final InputDTO minimalInput = InputDTO.builder()
            .rooms(minimalSimpleInput.getRooms())
            .plannedCourses(minimalSimpleInput.getPlannedCourses())
            .preferences(minimalSimpleInput.getPreferences())
            .week(SimpleInputDTO.buildCombinedWeek(minimalSimpleInput.getRooms()))
            .build();

    private static final SimpleInputDTO minimalSimpleInputWithPreference = MAPPER.readValue("{\"rooms\":[{\"roomNumber\":\"IF-01\",\"capacity\":200,\"week\":{\"days\":[{\"name\":\"Monday\",\"availableWindows\":[[{\"hour\":8,\"minute\":0},{\"hour\":9,\"minute\":0},{\"hour\":10,\"minute\":0},{\"hour\":11,\"minute\":0}]]}]}}],\"plannedCourses\":[{\"name\":\"Statistics\",\"presenterName\":\"John Doe\",\"numberOfListeners\":180,\"durationHours\":1,\"durationMinutes\":40}],\"preferences\":[\"1:John Doe:noClassesBefore:10:00\"]}",
            SimpleInputDTO.class);
    private static final InputDTO minimalInputWithPreference = InputDTO.builder()
            .rooms(minimalSimpleInputWithPreference.getRooms())
            .plannedCourses(minimalSimpleInputWithPreference.getPlannedCourses())
            .preferences(minimalSimpleInputWithPreference.getPreferences())
            .week(SimpleInputDTO.buildCombinedWeek(minimalSimpleInputWithPreference.getRooms()))
            .build();


    @Test
    @DisplayName("Generate with minimal input should return timetable with course in only available timeslot")
    void testGenerateWithMinimalInputShouldReturnTimetableWithCourseInOnlyAvailableTimeslot() {
        // Given
        TimeTableGenerator generator = new TimeTableGenerator();
        InputDTO input = minimalInput;
        Time expectedStartTime = input.getRooms().getFirst().getWeek().getDays().getFirst().getAvailableWindows().getFirst().getFirst();
        Time courseDuration = Time.of(input.getPlannedCourses().getFirst().durationHours(),
                input.getPlannedCourses().getFirst().durationMinutes());
        int expectedNrOfCourses = input.getPlannedCourses().size();


        // When
        OutputDTO output = generator.generate(input);

        // Then
        Assertions.assertNotNull(output);
        Assertions.assertEquals(expectedStartTime, output.courses().getFirst().startTime());
        Assertions.assertEquals(expectedStartTime.plus(courseDuration), output.courses().getFirst().endTime());
        Assertions.assertEquals(expectedNrOfCourses, output.courses().size());
    }

    @Test
    @DisplayName("Generate with minimal input with preference should return timetable with course not in not preferred timeslot")
    void testGenerateWithMinimalInputWithPreferenceShouldReturnTimetableWithCourseNotInNotPreferredTimeslot(){
        //Given
        TimeTableGenerator generator = new TimeTableGenerator();
        InputDTO input = minimalInputWithPreference;
        Time expectedStarTime = Time.of(String.join(":", input.getPreferences().getFirst().getParams()));
        Time courseDuration = Time.of(input.getPlannedCourses().getFirst().durationHours(),
                input.getPlannedCourses().getFirst().durationMinutes());

        //When
        OutputDTO output = generator.generate(input);

        //Then
        Assertions.assertNotNull(output);
        Assertions.assertEquals(expectedStarTime, output.courses().getFirst().startTime());
        Assertions.assertEquals(expectedStarTime.plus(courseDuration), output.courses().getFirst().endTime());
    }

}
