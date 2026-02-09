# Timetable Generator

A small Spring Boot application that generates timetables from JSON input(later from UI).

This project provides a REST API to generate timetables. It includes OpenAPI documentation and a Swagger UI so you can explore and test the API from your browser.

Quick links (when the app is running locally on port 8080):

- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## About OpenAPI

This project can expose OpenAPI and Swagger UI automatically using the springdoc-openapi library (for Spring Boot). When springdoc is on the classpath the application serves an OpenAPI JSON at `/v3/api-docs` and a Swagger UI at `/swagger-ui/index.html` by default.

Run the app

Using the bundled Maven wrapper on Windows (PowerShell):

```powershell
# build and run
.\mvnw.cmd spring-boot:run

# or build and run the jar
.\mvnw.cmd package
java -jar target\timetableGenerator-0.0.1-SNAPSHOT.jar
```

