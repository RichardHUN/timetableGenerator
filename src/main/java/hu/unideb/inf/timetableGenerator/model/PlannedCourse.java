package hu.unideb.inf.timetableGenerator.model;

public record PlannedCourse (
    String name,
    String presenterName,
    int numberOfListeners,
    int durationsHours,
    int durationMinutes
){}