package hu.unideb.inf.timetableGenerator.web.timetables;

import hu.unideb.inf.timetableGenerator.entity.TimetableEntity;
import hu.unideb.inf.timetableGenerator.entity.UserInfo;
import hu.unideb.inf.timetableGenerator.repository.UserInfoRepository;
import hu.unideb.inf.timetableGenerator.service.timetables.TimetablesService;
import hu.unideb.inf.timetableGenerator.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TimetablesControllerImpl implements TimetablesController {

    private final TimetablesService timetablesService;
    private final UserService userService;

    @Override
    public List<TimetableEntity> getAllTimetables() {
        UserInfo user = userService.getCurrentUser();
        return timetablesService.getTimetablesForUser(user);
    }

    @Override
    public void deleteTimetable(int id) {
        UserInfo user = userService.getCurrentUser();

        List<TimetableEntity> userTimetables = timetablesService.getTimetablesForUser(user);
        boolean owned = userTimetables.stream().anyMatch(t -> t.getId() == id);
        if (!owned) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Timetable not found or not owned by current user");
        }

        timetablesService.deleteTimetable(id);
    }

}

