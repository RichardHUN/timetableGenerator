package hu.unideb.inf.timetableGenerator.service;

import hu.unideb.inf.timetableGenerator.dto.InputDTO;
import hu.unideb.inf.timetableGenerator.dto.OutputDTO;
import hu.unideb.inf.timetableGenerator.dto.SimpleInputDTO;
import hu.unideb.inf.timetableGenerator.entity.TimetableEntity;
import lombok.NonNull;

public interface TimeTableService {

    TimetableEntity generateTimeTable(@NonNull InputDTO input);

    TimetableEntity generateTimeTableFromSimpleInput(@NonNull SimpleInputDTO input);

}
