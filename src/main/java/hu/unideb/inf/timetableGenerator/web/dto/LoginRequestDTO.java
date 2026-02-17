package hu.unideb.inf.timetableGenerator.web.dto;

public record LoginRequestDTO (
        String email,
        String name,
        String password,
        String roles
) {}
