package hu.unideb.inf.timetableGenerator.web.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api")
public interface UserController {

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<?> getUser(Authentication authentication);

}
