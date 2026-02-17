package hu.unideb.inf.timetableGenerator.service;

import hu.unideb.inf.timetableGenerator.domain.generator.TimeTableGenerator;
import hu.unideb.inf.timetableGenerator.dto.InputDTO;
import hu.unideb.inf.timetableGenerator.dto.OutputDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimeTableServiceImpl implements TimeTableService {

    //private final repository...
    private final TimeTableGenerator timeTableGenerator = new TimeTableGenerator();

    @Override
    public OutputDTO generateTimeTable(@NonNull InputDTO input) {
        return timeTableGenerator.generate(input);
    }

}
