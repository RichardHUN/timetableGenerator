# Timetable Generator

A small Spring Boot application that generates timetables from JSON input (later from UI).

An intelligent timetable generator for schools and universities that automatically schedules courses while respecting room availability, avoiding conflicts, and honoring presenter preferences.

This project provides a REST API to generate timetables. It includes OpenAPI documentation and a Swagger UI so you can explore and test the API from your browser.

Quick links (when the app is running locally on port 8080):

- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Swagger UI: http://localhost:8080/swagger

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17 or higher**: The application is built with Java and requires JDK 17+.
- **PostgreSQL Database**: The app uses PostgreSQL for user data storage.

### Database Setup

1. Install PostgreSQL (if not already installed).
2. Create a database named `TimeTableGenerator`.
3. Ensure the database is running on `localhost:5432` with username `postgres` and password `asd` (or update `src/main/resources/application.yaml` to match your configuration).

## Run the app

Using the bundled Maven wrapper on Windows (PowerShell):

```powershell
# build and run
.\mvnw.cmd spring-boot:run

# or build and run the jar
.\mvnw.cmd package
java -jar target\timetableGenerator-0.0.1-SNAPSHOT.jar
```

## API Endpoints

The application provides REST endpoints for authentication and timetable generation.

### Authentication Endpoints

These endpoints allow users to register and authenticate.

- **GET /api/auth/welcome**: Returns a welcome message. No authentication required.
- **POST /api/auth/register**: Registers a new user. 
  - Body: `{"username": "string", "password": "string"}`
  - Response: Success message or error if user exists.
- **POST /api/auth/login**: Authenticates a user and returns a JWT token.
  - Body: `{"username": "string", "password": "string"}`
  - Response: JWT token for authenticated requests.

### Timetable Endpoints

- **GET /api/health**: Health check endpoint. Returns application status. No authentication required.
- **GET /api/generate**: Generates a timetable based on input data. Requires authentication (JWT token in Authorization header).
  - Body: JSON object conforming to `InputDTO` (see example in `src/main/resources/input.json`).
  - Response: Generated timetable as `OutputDTO`.

### How to Check the Endpoints

1. Start the application as described above.
2. Open Swagger UI at http://localhost:8080/swagger-ui/index.html.
3. Use the interactive interface to test endpoints:
   - First, register a user via `/api/auth/register`.
   - Then, login via `/api/auth/login` to obtain a JWT token.
   - Copy the token and use it in the Authorization header (Bearer token) for authenticated endpoints like `/api/generate`.
   - Provide the input JSON for timetable generation.

Alternatively, use Postman to test the endpoints:

1. **Download and install Postman** from https://www.postman.com/downloads/ if you haven't already.
2. **Create a new collection** for the Timetable Generator API.
3. **Set up the base URL**: Use `http://localhost:8080` as the base URL for all requests.

#### Register a User
- Create a new POST request.
- URL: `{{base_url}}/api/auth/register`
- Headers: `Content-Type: application/json`
- Body (raw JSON):
  ```json
  {
    "username": "testuser",
    "password": "password"
  }
  ```
- Send the request. You should receive a success response.

#### Login and Get JWT Token
- Create a new POST request.
- URL: `{{base_url}}/api/auth/login`
- Headers: `Content-Type: application/json`
- Body (raw JSON):
  ```json
  {
    "username": "testuser",
    "password": "password"
  }
  ```
- Send the request. Copy the JWT token from the response body (e.g., `"token": "your_jwt_token_here"`).

#### Generate Timetable
- Create a new GET request (note: this endpoint uses GET with a request body, which is unconventional but supported in Postman).
- URL: `{{base_url}}/api/generate`
- Headers:
  - `Content-Type: application/json`
  - `Authorization: Bearer {{token}}` (replace `{{token}}` with the JWT token from login)
- Body (raw JSON): Paste the contents of `src/main/resources/input.json` or create your own InputDTO JSON.
- Send the request. You should receive the generated timetable as OutputDTO in the response.

## What It Does

The timetable generator takes your requirements as input and creates an optimized schedule that:
- ✓ Places courses in available rooms with sufficient capacity
- ✓ Avoids scheduling conflicts (no double-booking of presenters or rooms)
- ✓ Respects presenter preferences (e.g., "no classes before 10:00 AM")
- ✓ Finds the best solution when preferences conflict

## How It Works

### 1. Input
You provide:
- **Rooms**: Available rooms with their capacities and time slots
- **Courses**: Courses to schedule with presenter names, number of listeners, and duration
- **Preferences**: Optional constraints like "Jane Doe: no classes on Monday" with priority levels (1-10)

### 2. Smart Generation
The generator:
1. **Explores multiple options**: Creates up to 100 different schedule variations
2. **Avoids conflicts**: Never schedules two courses in the same room at the same time, or the same presenter in two places
3. **Respects important preferences**: High-priority preferences (5+) guide the placement decisions
4. **Scores each option**: Calculates a score based on how many preferences are violated

### 3. Intelligent Selection
The generator:
- Compares all generated schedules
- Selects the one with the **lowest score** (fewest/least important preference violations)
- Returns the optimal timetable

## Example

**Input:**
```
Rooms: 
  - Room 101 (capacity: 200, available Monday-Wednesday 8:00-20:00)
  - Room 102 (capacity: 30, available Monday-Tuesday 8:00-19:00)

Courses:
  - Statistics by John Doe (180 students, 1 hour 40 minutes)
  - Math by Jane Doe (24 students, 1 hour 30 minutes)

Preferences:
  - Priority 5: John Doe: no classes before 10:00 AM
  - Priority 3: Jane Doe: no classes on Monday
  - Priority 2: Jane Doe: no classes before 10:00 AM
```

**Output:**
```
✓ Statistics: Monday 10:00-11:40 in Room 101
✓ Math: Tuesday 10:00-11:30 in Room 102
✓ Score: 0 (all preferences satisfied!)
```

## Key Features

### Preference Priorities
- **Priority 1-4** (Low): Soft preferences - nice to have but can be violated if necessary
- **Priority 5-10** (High): Strong preferences - actively guides scheduling decisions

### Intelligent Search
The generator doesn't just find *any* valid schedule - it explores many possibilities to find the *best* one that minimizes preference violations.

### Conflict Prevention
Hard constraints (room capacity, time conflicts) are **never violated**. The generator only produces valid, conflict-free schedules.

## Understanding the Output

The output shows:
- **Each course** with its scheduled day, time, room, and presenter
- **Room availability** showing which time slots are still free after all courses are scheduled
- **Score** indicating how well preferences were satisfied (0 = perfect, higher = more violations)

## Technical Details

### Algorithm
The generator uses a **backtracking search with preference-based scoring**:
1. Recursively tries placing courses in different rooms and time slots
2. Prunes branches that violate high-priority preferences (after finding initial solutions)
3. Scores complete solutions based on all preference violations
4. Returns the solution with the best score

### Performance
- Generates up to 100 candidate solutions
- Typical runtime: seconds to minutes depending on problem complexity
- Configurable limits prevent excessive computation time

## Configuration

You can adjust the behavior by modifying `TimeTableGenerator.java`:

**Maximum solutions to explore:**
```java
private static final int MAX_SOLUTIONS = 100;  // Increase for more exploration
```

**High-priority threshold:**
```java
if (preference.getStrictness() >= 5 && ...)  // Change 5 to adjust threshold
```

## Example Scenarios

### Scenario 1: Simple Schedule
2 courses, 2 rooms, no preferences → Instant result

### Scenario 2: Competing Preferences
Multiple presenters want morning slots, limited rooms → Finds best compromise

### Scenario 3: Tight Constraints
Many courses, few rooms, strict preferences → Explores many options to find feasible solution

## Troubleshooting

**"Failed to generate timetable"**
- Not enough rooms or time slots for all courses
- Try: Add more rooms, expand time availability, or reduce courses

**"Preference not satisfied"**
- Low priority preferences may be violated if no better solution exists
- Try: Increase preference priority to 5 or higher

**Generation takes too long**
- Too many courses or rooms to explore
- Try: Reduce MAX_SOLUTIONS or simplify constraints

## Contributing

The codebase is organized as:
- `model/`: Data structures (Course, Room, Preference, etc.)
- `generator/`: Core timetable generation algorithm
- `runner/`: Input parsing and execution
- `runner/parser/`: Parsers for different input formats
