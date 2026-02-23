package hu.unideb.inf.timetableGenerator.domain.model;

public record PlannedCourse (
    String name,
    String presenterName,
    Integer numberOfListeners,
    Integer durationHours,
    Integer durationMinutes
){}