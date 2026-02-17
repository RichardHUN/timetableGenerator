package hu.unideb.inf.timetableGenerator.domain.model;

public record PlannedCourse (
    String name,
    String presenterName,
    int numberOfListeners,
    int durationsHours,
    int durationMinutes
){}