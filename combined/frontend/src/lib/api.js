// Thin wrapper around the Spring Boot backend's /tasks endpoints.
// Calls are scoped to a browser-local workspace ID; this is not authentication.

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

async function handle(res) {
  if (!res.ok) {
    // The backend returns { timestamp, status, error, fields? } on failure —
    // surface that message instead of a generic "request failed".
    const body = await res.json().catch(() => null);
    throw new Error(body?.error || `Request failed with status ${res.status}`);
  }
  if (res.status === 204) return null; // no content, e.g. after DELETE
  return res.json();
}

export function fetchTasks(userId) {
  return fetch(`${API_BASE}/tasks?userId=${encodeURIComponent(userId)}`).then(handle);
}

export function createTask(task) {
  return fetch(`${API_BASE}/tasks`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(task),
  }).then(handle);
}

export function updateTask(id, task) {
  return fetch(`${API_BASE}/tasks/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(task),
  }).then(handle);
}

export function toggleTaskDone(id) {
  return fetch(`${API_BASE}/tasks/${id}/done`, { method: "PATCH" }).then(handle);
}

export function deleteTask(id) {
  return fetch(`${API_BASE}/tasks/${id}`, { method: "DELETE" }).then(handle);
}
