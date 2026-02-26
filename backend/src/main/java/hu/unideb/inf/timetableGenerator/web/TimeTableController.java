package hu.unideb.inf.timetableGenerator.web;

import hu.unideb.inf.timetableGenerator.dto.InputDTO;
import hu.unideb.inf.timetableGenerator.dto.OutputDTO;
import hu.unideb.inf.timetableGenerator.dto.SimpleInputDTO;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TimeTableController {

    @GetMapping("/api/health")
    ResponseEntity<String> healthCheck();

    @PostMapping("/api/generate")
    ResponseEntity<OutputDTO> generateTimeTable(@RequestBody @NonNull InputDTO input);

    @PostMapping("/api/generate/simple")
    ResponseEntity<OutputDTO> generateTimeTableFromSimpleInput(@RequestBody @NonNull SimpleInputDTO input);

}
