# Timetable Generator
An intelligent timetable generator for schools and universities that automatically schedules courses while respecting room availability, avoiding conflicts, and honoring presenter preferences.

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

## How to Use

### Build
Open the project in **IntelliJ IDEA** and build it using the IDE's build tools.

### Run

There are two ways to run the generator:

**Option 1: Using MainFile with input.txt**
- Run the `MainFile` class
- Reads input from `input.txt` file
- Example input format in `input.txt`:
  - Room definitions (number, capacity, available days/times)
  - Course definitions (name, presenter, listeners, duration)
  - Preference definitions (priority:presenter:constraint:parameters)

**Option 2: Using MainArgs with command-line arguments**
- Run the `MainArgs` class
- Provide arguments in the same format as seen in `input.txt`
- Pass data directly as command-line arguments instead of reading from file

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
