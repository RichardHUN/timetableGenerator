package hu.unideb.inf.timetableGenerator.web;

import hu.unideb.inf.timetableGenerator.dto.InputDTO;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TimeTableController {

    @GetMapping("/api/health")
    ResponseEntity<?> healthCheck();

    @GetMapping("/api/generate")
    ResponseEntity<?> generateTimeTable(@RequestBody @NonNull InputDTO input);

}
