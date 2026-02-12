package hu.unideb.inf.timetableGenerator.web.dto;

import lombok.Builder;

@Builder
public record LoginResponseDTO (
    String email,
    String token
){}
