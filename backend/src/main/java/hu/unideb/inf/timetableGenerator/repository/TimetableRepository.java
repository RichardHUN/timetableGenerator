package hu.unideb.inf.timetableGenerator.repository;

import hu.unideb.inf.timetableGenerator.entity.TimetableEntity;
import hu.unideb.inf.timetableGenerator.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableEntity, Integer> {

    List<TimetableEntity> findAllByUser(UserInfo user);

}
