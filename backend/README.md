# Timetable Generator Backend

This is the backend service for the Timetable Generator application, a Spring Boot project that generates timetables based on input data such as available **rooms**, **courses**, and presenter **preferences**.

## Overview

The Timetable Generator backend provides a REST API for:
- Authenticating users
- Generating timetables while considering room availability, avoiding conflicts, and honoring presenter preferences.
- Storing a user's created timetables

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
- **Swagger UI**: `http://localhost:8080/swagger`

## API Endpoints

### Authentication

- **POST /api/auth/register**: Register a new user.
- **POST /api/auth/login**: Authenticate a user and return a JWT token.

### Timetable Generation

- **GET /api/generate**: Generate a timetable based on input data. *Needs auth. **Example input JSONs available in input folder:***
   - [medium input](../input/input-md.json)
   - [large input](../input/input-lg.json)


## Testing the API with Postman

- **Import the OpenAPI JSON:**
   - Start the backend (defaults to `http://localhost:8080`).
   - In Postman choose `File > Import` → `Link` and paste `http://localhost:8080/v3/api-docs`, or download the OpenAPI JSON and import the file.
   - Postman will create a new collection based on the OpenAPI spec containing requests for each endpoint.

- **Authentication (JWT):**
   - Create a Postman environment with variables `baseUrl` = `http://localhost:8080` and `token` (empty).
   - Use `POST /api/auth/login` to obtain a token. Set the `token` variable in the environment with the token value from the login response.

   - On protected requests add the `token` environment variable to the `Auth`/`Bearer Token` section.

This process creates a Postman collection from the OpenAPI JSON and lets you attach example requests/responses for each endpoint and response code.

---

## Troubleshooting

- **Database connection issues**: Ensure PostgreSQL is running and the connection details in `application.yaml` are correct.
- **Port conflicts**: If port 8080 is in use, update the port in `application.yaml` and restart the application.

---

## How it works
The user's inputted rooms' weeks get combined into one big **global week**. This week object
contains all available time slots, not considering the rooms they are from. 


The **generator** picks the first **Planned Course**, then the first available time slot 
from this **global week**.  
Then it picks
the corresponding room and time window *(meanwhile performs checks)*, then if all checks were
passed, occupies the time slot, creates the **Course** object from the **Planned Course**
and moves to the next planned course.

This is one cycle of one candidate.  
The generator creates many candidates, and the best one is chosen.  
The candidates are ordered by the number of **preference points** broken.  
If a candidate with **0 preference points broken** if found, the generator stops and returns it.

> *preference points* - the strictness of a preference