package hu.unideb.inf.timetableGenerator.service;

import hu.unideb.inf.timetableGenerator.dto.InputDTO;
import hu.unideb.inf.timetableGenerator.dto.OutputDTO;
import hu.unideb.inf.timetableGenerator.dto.SimpleInputDTO;
import lombok.NonNull;

public interface TimeTableService {

    OutputDTO generateTimeTable(@NonNull InputDTO input);

    OutputDTO generateTimeTableFromSimpleInput(@NonNull SimpleInputDTO input);

}
