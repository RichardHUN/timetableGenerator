package hu.unideb.inf.timetableGenerator.web;

import hu.unideb.inf.timetableGenerator.auth.Authorities;
import hu.unideb.inf.timetableGenerator.entity.AuthRequest;
import hu.unideb.inf.timetableGenerator.entity.UserInfo;
import hu.unideb.inf.timetableGenerator.service.JwtServiceImpl;
import hu.unideb.inf.timetableGenerator.exceptions.UserAlreadyExistsException;
import hu.unideb.inf.timetableGenerator.service.UserInfoService;
import hu.unideb.inf.timetableGenerator.web.dto.LoginRequestDTO;
import hu.unideb.inf.timetableGenerator.web.dto.LoginResponseDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {

    private UserInfoService service;
    private JwtServiceImpl jwtService;
    private AuthenticationManager authenticationManager;


    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/register")
    public ResponseEntity<?> addNewUser(@RequestBody LoginRequestDTO userInfo) {
        log.info("Received request to add new user: {}", userInfo.email());
        try {
            //If no roles are provided, assign a default role (e.g., "ROLE_USER")
            List<GrantedAuthority> roles;
            try {
                roles = (userInfo.roles() == null || userInfo.roles().isEmpty())
                ? List.of(new Authorities.UserAuthority())
                : Authorities.getGrantedAuthorities(userInfo.roles());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid roles provided for user: {}. Error: {}", userInfo.email(), e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid roles provided: "+ userInfo.roles() +". Valid roles are: ROLE_USER, ROLE_ADMIN");
            }

            log.info("User registered successfully: {}:{} with roles: {}", userInfo.email(), userInfo.password(), roles);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                        service.addUser(
                            UserInfo.builder()
                                .email(userInfo.email())
                                .name(userInfo.name())
                                .password(userInfo.password())
                                .roles(roles)
                            .build()
                        )
                    );
        } catch (UserAlreadyExistsException e) {
            log.warn("User already exists: {}", userInfo.email());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this email already exists");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        log.info("Received authentication request for user: {}:{}", authRequest.getEmail(), authRequest.getPassword());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            log.warn("Authentication failed: {}:{}", authRequest.getEmail(), authRequest.getPassword());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        if (authentication.isAuthenticated()) {
            log.info("Authentication Successful: {}", authentication.getName());
            return ResponseEntity.ok(
                    LoginResponseDTO.builder()
                            .email(authentication.getName())
                            .token(jwtService.generateToken(authRequest.getEmail()))
                    .build());
        }

        log.error("Unexpected error occurred during login. {}:{}", authRequest.getEmail(), authRequest.getPassword());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("We are sorry! An unexpected error occurred during login process. Please try again later!");
    }
}
