package hu.unideb.inf.timetableGenerator.service.dao;

import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * This record represents the standard user information data access object (DAO) used in the timetable generator application.
 * It contains the username, email, and roles of a user.
 * @param username the username of the user
 * @param email the email address of the user
 * @param roles the list of granted authorities (roles) assigned to the user
 */
@Builder
public record StandardUserInfoDAO(
        String username,
        String email,
        List<GrantedAuthority> roles
){ }
