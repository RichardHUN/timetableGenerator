# Timetable Generator

This repository contains two top-level projects:

- `backend/` - Spring Boot timetable generator service (Spring, Java, Maven) - [Backend Docs](backend/README.md)
- `frontend/` - SvelteKit frontend - [Frontend Docs](frontend/README.md)

## Backend

Basic info about the backend:
- Built with Spring Boot (Java)
- Provides REST API for timetable generation and user management  

### How to run the backend
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

See [Backend Docs](backend/README.md) for more details and features.

## Frontend

Basic info about the frontend:
- Built with SvelteKit
- Provides user authentication (register, login, logout)
- Timetable generation interface
- User profile display
- Stored Timetables view
- Responsive navigation bar with theme toggle
- Integrates with backend API for authentication and user data

To run the frontend locally:
1. Change directory into the frontend:
	```powershell
	cd frontend
	```
1. Copy the `.env.example` file to `.env` and update the API base URL if needed:
	```powershell
	copy .env.example .env
	```
1. Install dependencies:
	```powershell
	npm install
	```
1. Start the development server:
	```powershell
	npm run dev -- --open
	```
See [Frontend Docs](frontend/README.md) for more details and features.

---

## Example input JSON available in `input` folder: 
- [medium input](input/input-md.json) 
- [large input](input/input-lg.json)