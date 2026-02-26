package hu.unideb.inf.timetableGenerator.service;

import hu.unideb.inf.timetableGenerator.domain.generator.TimeTableGenerator;
import hu.unideb.inf.timetableGenerator.domain.model.Week;
import hu.unideb.inf.timetableGenerator.dto.InputDTO;
import hu.unideb.inf.timetableGenerator.dto.OutputDTO;
import hu.unideb.inf.timetableGenerator.dto.SimpleInputDTO;
import hu.unideb.inf.timetableGenerator.entity.UserInfo;
import hu.unideb.inf.timetableGenerator.repository.UserInfoRepository;
import hu.unideb.inf.timetableGenerator.service.timetables.TimetablesService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

import static hu.unideb.inf.timetableGenerator.dto.SimpleInputDTO.buildCombinedWeek;

@Service
@Slf4j
@RequiredArgsConstructor
public class TimeTableServiceImpl implements TimeTableService {

    private final TimeTableGenerator timeTableGenerator;
    private final TimetablesService timetablesService;
    private final UserInfoRepository userInfoRepository;

    @Override
    public OutputDTO generateTimeTable(@NonNull InputDTO input) {
        OutputDTO output = timeTableGenerator.generate(input);
        saveForCurrentUser(output);
        return output;
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

        OutputDTO output = timeTableGenerator.generate(fullInput);
        log.info("Timetable generated successfully");
        saveForCurrentUser(output);
        log.info("Timetable persisted to DB successfully");
        return output;
    }

    private void saveForCurrentUser(OutputDTO output) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInfo user = userInfoRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + email));
        timetablesService.saveTimetable(user, output);
    }

}
