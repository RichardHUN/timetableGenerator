package hu.unideb.inf.timetableGenerator.service.dao;

import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Builder
public record StandardUserInfoDAO(
        String username,
        String email,
        List<GrantedAuthority> roles
){ }
