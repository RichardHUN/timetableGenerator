package hu.unideb.inf.timetableGenerator.web;

import hu.unideb.inf.timetableGenerator.entity.AuthRequest;
import hu.unideb.inf.timetableGenerator.web.dto.LoginRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
public interface AuthenticationController {

    @GetMapping("/welcome")
    String welcome();

    @PostMapping("/register")
    ResponseEntity<?> addNewUser(@RequestBody LoginRequestDTO userInfo);

    @PostMapping("/login")
    ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest);

}
