package hu.unideb.inf.timetableGenerator.service;

import io.jsonwebtoken.Claims;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {

    String generateToken(String email);

    String extractUsername(String token);

    Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    Boolean validateToken(String token, @NonNull UserDetails userDetails);

}
