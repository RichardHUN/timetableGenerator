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

Basic info about the frontend:
- Built with SvelteKit (modern web framework)
- Provides user authentication (register, login, logout)
- Timetable generation interface
- User profile display
- Responsive navigation bar with theme toggle
- Integrates with backend API for authentication and user data

To run the frontend locally:
1. Change directory into the frontend:
	```powershell
	cd frontend
	```
2. Start the development server:
	```powershell
	npm run dev -- --open
	```
See `frontend/README.md` for more details, features, and development scripts.