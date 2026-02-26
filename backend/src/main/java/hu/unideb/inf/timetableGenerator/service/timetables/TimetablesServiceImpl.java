package hu.unideb.inf.timetableGenerator.service.timetables;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.unideb.inf.timetableGenerator.dto.OutputDTO;
import hu.unideb.inf.timetableGenerator.entity.TimetableEntity;
import hu.unideb.inf.timetableGenerator.entity.UserInfo;
import hu.unideb.inf.timetableGenerator.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimetablesServiceImpl implements TimetablesService {

    private final TimetableRepository timetableRepository;
    private final ObjectMapper objectMapper;

    @Override
    public TimetableEntity getTimetable(int id) throws  IllegalStateException {
        return timetableRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Timetable not found")
        );
    }

    @Override
    public List<TimetableEntity> getTimetablesForUser(UserInfo user) {
        return timetableRepository.findAllByUser(user);
    }

    @Override
    public void saveTimetable(UserInfo user, OutputDTO output) {
        try {
            String json = objectMapper.writeValueAsString(output);
            TimetableEntity entity = TimetableEntity.builder()
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .timetableJson(json)
                    .build();
            timetableRepository.save(entity);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize timetable", e);
        }
    }

    @Override
    public void deleteTimetable(int id) {
        timetableRepository.deleteById(id);
    }

    @Override
    public TimetableEntity renameTimetable(int id, String newName) {
        TimetableEntity timetable = timetableRepository.findById(id).orElseThrow(() -> new IllegalStateException("Timetable not found"));

        timetable.setName(newName);
        timetableRepository.save(timetable);

        return timetableRepository.findById(id).orElseThrow(() -> new IllegalStateException("Timetable not found"));
    }
}
