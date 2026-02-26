package hu.unideb.inf.timetableGenerator.service;

import hu.unideb.inf.timetableGenerator.domain.generator.TimeTableGenerator;
import hu.unideb.inf.timetableGenerator.domain.model.Week;
import hu.unideb.inf.timetableGenerator.dto.InputDTO;
import hu.unideb.inf.timetableGenerator.dto.OutputDTO;
import hu.unideb.inf.timetableGenerator.dto.SimpleInputDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static hu.unideb.inf.timetableGenerator.dto.SimpleInputDTO.buildCombinedWeek;

@Service
@RequiredArgsConstructor
public class TimeTableServiceImpl implements TimeTableService {

    //private final repository...
    private final TimeTableGenerator timeTableGenerator;

    @Override
    public OutputDTO generateTimeTable(@NonNull InputDTO input) {
        return timeTableGenerator.generate(input);
    }

    @Override
    public OutputDTO generateTimeTableFromSimpleInput(@NonNull SimpleInputDTO input) {
        Week week = buildCombinedWeek(input.getRooms());

        InputDTO fullInput =
                InputDTO.builder()
                .week(week)
                .rooms(input.getRooms())
                .plannedCourses(input.getPlannedCourses())
                .preferences(input.getPreferences())
                .build();

        return timeTableGenerator.generate(fullInput);
    }



}
