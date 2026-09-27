# Task Scheduler — Full Stack

A task-scheduling web app: React (Vite + Tailwind) frontend talking to a Spring
Boot backend that stores tasks.

```
task-scheduler/
├── frontend/   # React dashboard — calendar, task list, timer
└── backend/    # Spring Boot REST API — H2 locally, PostgreSQL when hosted
```

**Who does what:**
- **The Java backend** stores and serves task data. Every request is scoped by a
  browser-local workspace ID, which is not an authentication mechanism.
- Theme preference (light/dark) still lives in the browser's `localStorage` — it's
  per-device display state, not data worth a network round trip.

## Running it locally
You need both halves running at the same time, in two terminals.

**Terminal 1 — backend:**
```bash
cd backend
mvn spring-boot:run
```
Runs on `http://localhost:8080`.

**Terminal 2 — frontend:**
```bash
cd frontend
npm install
npm run dev
```
Runs on `http://localhost:5173`. Open that URL in your browser.

The frontend's `.env` already points at `VITE_API_BASE_URL=http://localhost:8080`
(see `frontend/.env.example` for the template). If you deploy the backend somewhere
else later, that's the one value to change.

## Hosting preview

Deployment configuration is provided for Render (Spring Boot + PostgreSQL) and
Vercel (Vite frontend). These steps create a hosted preview; they do not add user
authentication.

### Backend and database on Render

1. Create a Render Blueprint from the repository root and review the paid
  web-service and PostgreSQL plans before confirming. Render needs a Dockerfile
  build using Java 25.
2. After the Vercel site is created, set the Render service variable
  `CORS_ALLOWED_ORIGINS` to the exact frontend origin, such as
  `https://your-project.vercel.app` (no trailing slash), then redeploy.
3. The backend uses the `prod` Spring profile and the managed PostgreSQL database.
  The H2 console is disabled in this profile.

### Frontend on Vercel

1. Import the repository and set the Vercel project root directory to `combined/frontend`.
2. Set the build command to `npm run build` and output directory to `dist`.
3. Add `VITE_API_BASE_URL` with the Render service URL, for example
  `https://your-api.onrender.com` (no trailing slash), then deploy.

### Before sharing publicly

The API currently accepts a browser-local `userId`; it does not authenticate users
or authorize access to task records. Anyone who can reach the API can call its task
endpoints. Do not use this deployment for private or sensitive tasks until proper
authentication and per-user authorization are implemented.

## How a task gets from the UI to the database
1. You fill out `TaskModal` and hit **Create**.
2. `TaskPanel.jsx` calls `createTask()` in `frontend/src/lib/api.js`, which `POST`s
  to `http://localhost:8080/tasks` with the browser-local workspace ID as `userId`.
3. `TaskController` (backend) validates the request, saves it via `TaskRepository`,
   and returns the saved task — now with a database-assigned `id`.
4. `TaskPanel` calls `onTasksChanged()`, which re-fetches the task list from
   `Dashboard.jsx` so the UI reflects what's actually in the database.

Same pattern for editing, checking off, and deleting a task — the frontend never
guesses at an id or holds state the backend doesn't know about; it always reflects
what the server returns.

## Current limitations
- **Local data doesn't persist across backend restarts.** Local development uses
  in-memory H2. The Render production profile is configured to use PostgreSQL.
- **No authentication or authorization.** Any request with any `userId` is accepted;
  the browser-local workspace ID only separates task lists and must not be treated
  as a security boundary.
- **CORS must be configured for the deployed frontend.** Set
  `CORS_ALLOWED_ORIGINS` to the exact frontend origin in the backend environment.

See `backend/README.md` for the full API reference.
