package hu.unideb.inf.timetableGenerator.repository;

import hu.unideb.inf.timetableGenerator.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByEmail(String email);
    boolean existsByEmail(String email);
}
