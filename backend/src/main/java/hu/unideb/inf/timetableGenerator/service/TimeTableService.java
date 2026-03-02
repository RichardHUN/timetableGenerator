package hu.unideb.inf.timetableGenerator.service;

import hu.unideb.inf.timetableGenerator.dto.InputDTO;
import hu.unideb.inf.timetableGenerator.dto.SimpleInputDTO;
import hu.unideb.inf.timetableGenerator.entity.TimetableEntity;
import lombok.NonNull;

/**
 * This interface defines the contract for the TimeTableService, which is responsible for generating timetables based on the provided input data.
 * It includes methods for generating timetables from both detailed and simple input formats.
 */
public interface TimeTableService {

    TimetableEntity generateTimeTable(@NonNull InputDTO input);

    TimetableEntity generateTimeTableFromSimpleInput(@NonNull SimpleInputDTO input);

}
