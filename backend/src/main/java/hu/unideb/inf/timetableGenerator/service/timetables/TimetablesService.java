package hu.unideb.inf.timetableGenerator.service.timetables;

import hu.unideb.inf.timetableGenerator.dto.OutputDTO;
import hu.unideb.inf.timetableGenerator.entity.TimetableEntity;
import hu.unideb.inf.timetableGenerator.entity.UserInfo;

import java.util.List;

public interface TimetablesService {

    TimetableEntity getTimetable(int id) throws IllegalStateException;

    List<TimetableEntity> getTimetablesForUser(UserInfo user);

    TimetableEntity saveTimetable(UserInfo user, OutputDTO output);

    void deleteTimetable(int id);

    TimetableEntity renameTimetable(int id, String newName) throws IllegalArgumentException;

}
