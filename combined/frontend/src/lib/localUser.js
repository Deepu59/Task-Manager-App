const USER_ID_KEY = "task_scheduler_user_id";

export function getLocalUser() {
  let uid = localStorage.getItem(USER_ID_KEY);
  if (!uid) {
    uid = crypto.randomUUID();
    localStorage.setItem(USER_ID_KEY, uid);
  }

  return { uid, displayName: "Local workspace" };
}