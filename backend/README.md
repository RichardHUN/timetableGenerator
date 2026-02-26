# Timetable Generator Backend

This is the backend service for the Timetable Generator application, a Spring Boot project that generates timetables based on input data such as available rooms, courses, and presenter preferences.

## Overview

The Timetable Generator backend provides a REST API for:
- Authenticating users
- Generating timetables while considering room availability, avoiding conflicts, and honoring presenter preferences.
- ~~Storing a user's created timetables~~

## Technologies Used
- **Java 17**: The application is built with Java and requires JDK 17+.
- **Spring Boot**: Framework for building the REST API.
- **PostgreSQL**: Database for storing user data.

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17 or higher**
- **PostgreSQL Database**

### Database Setup

1. Install PostgreSQL (if not already installed).
2. Create a database named `TimeTableGenerator`.
3. Ensure the database is running on `localhost:5432` with username `postgres` and password `asd` (or update `src/main/resources/application.yaml` to match your configuration).

## Running the Application

Using the bundled Maven wrapper:

```bash
# Build and run the application
./mvnw spring-boot:run

# Alternatively, build the jar and run
./mvnw package
java -jar target/timetableGenerator-0.0.1-SNAPSHOT.jar
```

## API Documentation

The API is documented with OpenAPI and can be accessed via Swagger UI when the application is running.

- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

## API Endpoints

### Authentication

- **POST /api/auth/register**: Register a new user.
- **POST /api/auth/login**: Authenticate a user and return a JWT token.

### Timetable Generation

- **GET /api/generate**: Generate a timetable based on input data. *Needs auth*.

## Example Usage

1. Register a new user:
   ```bash
   curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d '{"username": "testuser", "password": "password"}'
   ```

2. Login to obtain a JWT token:
   ```bash
   curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username": "testuser", "password": "password"}'
   ```

3. Generate a timetable (replace `{{token}}` with the actual token):
   ```bash
   curl -X GET http://localhost:8080/api/generate -H "Authorization: Bearer {{token}}" -H "Content-Type: application/json" -d @input.json
   ```

## Troubleshooting

- **Database connection issues**: Ensure PostgreSQL is running and the connection details in `application.yaml` are correct.
- **Port conflicts**: If port 8080 is in use, update the port in `application.yaml` and restart the application.

---