package hu.unideb.inf.timetableGenerator.web;

import hu.unideb.inf.timetableGenerator.dto.InputDTO;
import hu.unideb.inf.timetableGenerator.dto.OutputDTO;
import hu.unideb.inf.timetableGenerator.dto.SimpleInputDTO;
import hu.unideb.inf.timetableGenerator.entity.UserInfo;
import hu.unideb.inf.timetableGenerator.service.TimeTableService;
import hu.unideb.inf.timetableGenerator.service.timetables.TimetablesService;
import hu.unideb.inf.timetableGenerator.service.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TimeTableControllerImpl implements TimeTableController {

    private final TimeTableService timeTableService;
    private final UserService userService;
    private final TimetablesService timetablesService;

    @Override
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok("API working correctly!");
    }

    @Override
    public ResponseEntity<?> generateTimeTable(@RequestBody @NonNull InputDTO input) {
        log.info("Timetable generation requested received");

        log.info(input.toString());

        if( input.getPlannedCourses() == null || input.getWeek() == null || input.getRooms() == null || input.getPreferences() == null ) {
            log.error( "Invalid input. Missing parameters");
            return ResponseEntity.badRequest().body("Invalid input. Missing parameters.");
        }

        try {
            OutputDTO output = timeTableService.generateTimeTable(input);
            log.info("Timetable generated successfully");
            return ResponseEntity.ok(output);
        } catch (IllegalStateException e) {
            log.error("No timetable can be generated with given input with the given amount of steps");
            return ResponseEntity.badRequest()
                    .body("No timetable can be generated with given input with the given amount of steps.");
        } catch (Exception e) {
            log.error("Error generating timetable: ", e);
            return ResponseEntity.badRequest()
                    .body("Couldn't generate timetable with given input. Please check the input and try again.");
        }
    }

    @Override
    public ResponseEntity<?> generateTimeTableFromSimpleInput(@NonNull SimpleInputDTO input) {
        log.info("Timetable generation requested received(simple)");

        log.info(input.toString());

        if( input.getPlannedCourses() == null || input.getRooms() == null || input.getPreferences() == null ) {
            log.error( "Invalid input. Missing parameters");
            return ResponseEntity.badRequest().body("Invalid input. Missing parameters.");
        }

        try {
            OutputDTO output = timeTableService.generateTimeTableFromSimpleInput(input);
            log.info("Timetable generated successfully");
            return ResponseEntity.ok(output);
        } catch (IllegalStateException e) {
            log.error("No timetable can be generated with given input with the given amount of steps");
            return ResponseEntity.badRequest()
                    .body("No timetable can be generated with given input with the given amount of steps.");
        } catch (Exception e) {
            log.error("Error generating timetable: ", e);
            return ResponseEntity.badRequest()
                    .body("Couldn't generate timetable with given input. Please check the input and try again.");
        }
    }

}
