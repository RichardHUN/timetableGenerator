package hu.unideb.inf.timetableGenerator.service.user;

import hu.unideb.inf.timetableGenerator.entity.UserInfo;
import hu.unideb.inf.timetableGenerator.repository.UserInfoRepository;
import hu.unideb.inf.timetableGenerator.service.dao.StandardUserInfoDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserInfoRepository repository;

    @Override
    public StandardUserInfoDAO getUserInfo(String email) {
        Optional<UserInfo> user = repository.findByEmail(email);

        if(user.isEmpty()) { throw new IllegalStateException("User not found"); }

        return StandardUserInfoDAO
            .builder()
                .username(user.get().getName())
                .email(user.get().getEmail())
                .roles(user.get().getRoles())
            .build();
    }

}
