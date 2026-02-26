package hu.unideb.inf.timetableGenerator.web;

import hu.unideb.inf.timetableGenerator.dto.InputDTO;
import hu.unideb.inf.timetableGenerator.dto.OutputDTO;
import hu.unideb.inf.timetableGenerator.dto.SimpleInputDTO;
import hu.unideb.inf.timetableGenerator.entity.TimetableEntity;
import hu.unideb.inf.timetableGenerator.service.TimeTableService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TimeTableControllerImpl implements TimeTableController {

    private final TimeTableService timeTableService;

    @Override
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("API working correctly!");
    }

    @Override
    public ResponseEntity<TimetableEntity> generateTimeTable(@RequestBody @NonNull InputDTO input) {
        log.info("Timetable generation requested received");

        log.info(input.toString());

        if( input.getPlannedCourses() == null || input.getWeek() == null || input.getRooms() == null || input.getPreferences() == null ) {
            log.error( "Invalid input. Missing parameters");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input. Missing parameters.");
        }

        try {
            TimetableEntity output = timeTableService.generateTimeTable(input);
            log.info("Timetable generated successfully");
            return ResponseEntity.ok(output);
        } catch (IllegalStateException e) {
            log.error("No timetable can be generated with given input with the given amount of steps");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No timetable can be generated with given input with the given amount of steps.");
        } catch (Exception e) {
            log.error("Error generating timetable: ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Couldn't generate timetable with given input. Please check the input and try again.");
        }
    }

    @Override
    public ResponseEntity<TimetableEntity> generateTimeTableFromSimpleInput(@NonNull SimpleInputDTO input) {
        log.info("Timetable generation requested received(simple)");

        log.info(input.toString());

        if( input.getPlannedCourses() == null || input.getRooms() == null || input.getPreferences() == null ) {
            log.error("Invalid input. Missing parameters");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input. Missing parameters.");
        }

        try {
            TimetableEntity output = timeTableService.generateTimeTableFromSimpleInput(input);
            log.info("Timetable generated successfully");
            return ResponseEntity.ok(output);
        } catch (IllegalStateException e) {
            log.error("No timetable can be generated with given input with the given amount of steps");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No timetable can be generated with given input with the given amount of steps.");
        } catch (Exception e) {
            log.error("Error generating timetable: ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Couldn't generate timetable with given input. Please check the input and try again.");
        }
    }

}
