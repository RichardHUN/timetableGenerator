package hu.unideb.inf.timetableGenerator.web.user;

import hu.unideb.inf.timetableGenerator.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @GetMapping("/user")
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUser(Authentication authentication) {
        log.info("Received request for user info: {}", authentication.getName());
        return ResponseEntity.ok(userService.getUserInfo(authentication.getName()));
    }

}
