# Timetable Generator (monorepo)

This repository contains two top-level projects:

- `backend/` — the existing Spring Boot timetable generator service (Java, Maven).
- `frontend/` — placeholder for the frontend application (you can scaffold React/Angular/Vue here).

How to run the backend

1. Change directory into the backend:

```powershell
cd backend
```

2. Use the bundled Maven wrapper to build and run the app:

```powershell
# build and run
.\mvnw.cmd spring-boot:run

# or build and run the jar
.\mvnw.cmd package
java -jar target\timetableGenerator-0.0.1-SNAPSHOT.jar
```

Frontend

The `frontend/` folder is intentionally left empty as a placeholder. See `frontend/README.md` for suggested stacks and basic instructions.

Notes

- The Git repository (history) remains at the root of this repo.
- Build artifacts and IDE folders for the backend are ignored in `.gitignore`.

