package hu.unideb.inf.timetableGenerator.web;

import hu.unideb.inf.timetableGenerator.entity.AuthRequest;
import hu.unideb.inf.timetableGenerator.entity.UserInfo;
import hu.unideb.inf.timetableGenerator.web.dto.LoginRequestDTO;
import hu.unideb.inf.timetableGenerator.web.dto.LoginResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
public interface AuthenticationController {

    @PostMapping("/register")
    ResponseEntity<UserInfo> addNewUser(@RequestBody LoginRequestDTO userInfo);

    @PostMapping("/login")
    ResponseEntity<LoginResponseDTO> authenticateAndGetToken(@RequestBody AuthRequest authRequest);

}
