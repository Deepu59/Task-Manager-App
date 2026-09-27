# Task Scheduler

A full-stack planner for organizing tasks by date, tracking focus time, and reviewing monthly progress. The application combines a React dashboard with a Spring Boot REST API and supports PostgreSQL for hosted deployments.

## Features

- Browse tasks on a monthly calendar
- Create, edit, complete, and delete tasks
- Organize work by category and priority
- Search tasks and switch between light and dark themes
- Use the focus timer and monthly summary views

## Technology

| Area | Technologies |
| --- | --- |
| Frontend | React 19, Vite, Tailwind CSS, React Router |
| Backend | Java 25, Spring Boot 3.5, Spring Data JPA |
| Local database | H2 in-memory database |
| Hosted database | PostgreSQL |
| Deployment | Vercel frontend, Render backend and database |

## Project structure

```text
.
├── combined/
│   ├── backend/       Spring Boot API
│   └── frontend/      React application
└── render.yaml        Render Blueprint
```

## Run locally

Requirements: Node.js 22 or later, JDK 25, and Maven 3.9 or later.

Start the backend in one terminal:

```powershell
cd combined/backend
mvn spring-boot:run
```

Start the frontend in another terminal:

```powershell
cd combined/frontend
npm install
npm run dev
```

Open [http://localhost:5173](http://localhost:5173). Use `localhost` rather than `127.0.0.1` so the development CORS configuration matches. The frontend API URL defaults to `http://localhost:8080`; set `VITE_API_BASE_URL` to change it.

Local development uses an in-memory H2 database, so its data is cleared when the backend stops.

## API overview

The REST API runs on port `8080` by default.

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/tasks?userId={id}` | List tasks for a workspace ID |
| `POST` | `/tasks` | Create a task |
| `PUT` | `/tasks/{id}` | Update a task |
| `PATCH` | `/tasks/{id}/done` | Toggle completion |
| `DELETE` | `/tasks/{id}` | Delete a task |

## Hosting

Deployment files are included for Vercel and Render. The Render Blueprint uses free web-service and PostgreSQL plans. Free Render web services sleep after 15 minutes without traffic, and free PostgreSQL expires after 30 days (with a limited recovery window); this setup is for a demo, not durable production data. Vercel Hobby is free for personal, non-commercial projects. Configure `VITE_API_BASE_URL` in Vercel with the Render API URL and `CORS_ALLOWED_ORIGINS` in Render with the exact Vercel site origin.

See the [deployment and development guide](combined/README.md) for setup details and environment variables.

## Security status

The API does not currently authenticate users or authorize task access. Its `userId` value is a browser-local workspace identifier, not a security boundary. Do not expose the hosted API for private or sensitive data until authentication and per-user authorization are implemented.
