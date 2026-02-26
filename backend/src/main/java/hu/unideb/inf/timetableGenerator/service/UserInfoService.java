package hu.unideb.inf.timetableGenerator.service;

import hu.unideb.inf.timetableGenerator.entity.UserInfo;
import hu.unideb.inf.timetableGenerator.exceptions.UserAlreadyExistsException;
import hu.unideb.inf.timetableGenerator.repository.UserInfoRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserInfoService implements UserDetailsService {

    private final UserInfoRepository repository;
    private final PasswordEncoder encoder;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        Optional<UserInfo> userInfo = repository.findByEmail(email);

        if (userInfo.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Convert UserInfo to UserDetails (UserInfoDetails)
        UserInfo user = userInfo.get();
        return new User(user.getEmail(), user.getPassword(), user.getRoles());
    }

    public UserInfo addUser(UserInfo userInfo) throws UserAlreadyExistsException {
        if( repository.existsByEmail(userInfo.getEmail()) ) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        return repository.save(userInfo);
    }
}
