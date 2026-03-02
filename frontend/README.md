# Timetable Generator - Frontend

This folder contains the frontend application of the Timetable Generator project. The frontend is built using SvelteKit, a modern framework for building web applications.

## Setting up the Frontend
1. Copy the `.env.example` file to `.env` and update the API base URL if needed:
	```powershell
	copy .env.example .env
	```
1. Install dependencies:
	```bash
	npm install
	```

## Running the Frontend

To run the frontend application locally, use the following command:

```bash
npm run dev -- --open
```

This will start a development server and automatically open the application in your default web browser.

## Example input JSONs available in `input` folder:
- [medium input](../input/input-md.json)
- [large input](../input/input-lg.json)

## Features

The frontend provides:
- User authentication (register, login, logout)
- Timetable generation interface
- Display of generated timetable
- User profile display
- Stored Timetables view
- Responsive navigation bar with theme toggle
- Integration with backend API for authentication and user data

## Available Routes
- `/` - Home page
- `/generate` - Timetable generation page
- `/result` - Generated timetable display page
- `/login` - Login page
- `/register` - Registration page
- `/user` - User profile page
- `/docs` - Input specification documentation page
- `/timetables` - Stored timetables page

## Project Structure

- `src/routes/` - SvelteKit pages and layouts
- `src/lib/api.ts` - API functions for authentication
- `src/lib/Nav.svelte` - Navigation bar component
- `static/` - Static assets

## API Integration
The frontend communicates with the backend via REST API endpoints for authentication and user data. API base URL is configured via environment variables.

## Development Scripts
- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Check code style
- `npm run format` - Format code
