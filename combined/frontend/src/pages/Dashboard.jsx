import { useCallback, useEffect, useMemo, useState } from "react";
import { fetchTasks } from "../lib/api";
import { getLocalUser } from "../lib/localUser";

import Sidebar from "../dashboard/components/Sidebar";
import TopBar from "../dashboard/components/TopBar";
import CalendarMonth from "../dashboard/components/CalendarMonth";
import TaskPanel from "../dashboard/components/TaskPanel";
import TimerPanel from "../dashboard/components/TimerPanel";
import MonthlySummary from "../dashboard/components/MonthlySummary";

import { useUserStorage } from "../dashboard/hooks/useUserStorage";
import { useTheme } from "../dashboard/hooks/useTheme";

export default function Dashboard() {
  const [user] = useState(getLocalUser);
  const [active, setActive] = useState("tasks");

  const [selectedDate, setSelectedDate] = useState(new Date());
  const [monthDate, setMonthDate] = useState(new Date());
  const [query, setQuery] = useState("");

  const uid = user?.uid;

  // Tasks now live in the Spring Boot backend, not localStorage. `tasksLoading`/
  // `tasksError` let TaskPanel show a spinner or a retry message instead of
  // silently rendering an empty list while the request is in flight or failed.
  const [tasks, setTasks] = useState([]);
  const [tasksLoading, setTasksLoading] = useState(true);
  const [tasksError, setTasksError] = useState(null);

  const loadTasks = useCallback(() => {
    if (!uid) return;
    setTasksLoading(true);
    setTasksError(null);
    fetchTasks(uid)
      .then(setTasks)
      .catch((err) => setTasksError(err.message))
      .finally(() => setTasksLoading(false));
  }, [uid]);

  useEffect(() => {
    loadTasks();
  }, [loadTasks]);

  // preferences per user (theme) — this one stays in localStorage; it's device-local
  // display state, not data worth putting behind a network call.
  const [prefs, setPrefs] = useUserStorage(uid, "ts_prefs_v1", { theme: "light" });
  const theme = prefs?.theme || "light";
  useTheme(theme);

  const taskCountByDate = useMemo(() => {
    const map = {};
    for (const t of tasks) map[t.date] = (map[t.date] || 0) + 1;
    return map;
  }, [tasks]);

  function toggleTheme() {
    setPrefs((p) => ({
      ...p,
      theme: (p?.theme || "light") === "dark" ? "light" : "dark",
    }));
  }

  return (
    <div className="min-h-screen bg-sky-50 text-slate-900 dark:bg-slate-900 dark:text-slate-100">
      <div className="mx-auto flex min-h-screen max-w-7xl flex-col md:flex-row">
        <Sidebar active={active} setActive={setActive} />

        <main className="flex-1">
          <TopBar
            selectedDate={selectedDate}
            onToday={() => {
              const now = new Date();
              setSelectedDate(now);
              setMonthDate(now);
            }}
            query={query}
            setQuery={setQuery}
            theme={theme}
            toggleTheme={toggleTheme}
            user={user}
            showSearch={active === "tasks"}
          />

          <div className="p-4">
            {active === "tasks" ? (
              tasksError ? (
                <div className="rounded-2xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-700">
                  Couldn't load tasks: {tasksError}.{" "}
                  <button className="font-semibold underline" onClick={loadTasks}>
                    Retry
                  </button>
                </div>
              ) : (
                <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
                  <CalendarMonth
                    monthDate={monthDate}
                    setMonthDate={setMonthDate}
                    selectedDate={selectedDate}
                    onSelectDate={(d) => {
                      setSelectedDate(d);
                      setMonthDate(d);
                    }}
                    taskCountByDate={taskCountByDate}
                  />

                  <TaskPanel
                    tasks={tasks}
                    userId={uid}
                    loading={tasksLoading}
                    onTasksChanged={loadTasks}
                    selectedDate={selectedDate}
                    query={query}
                  />
                </div>
              )
            ) : null}

            {active === "timer" ? <TimerPanel /> : null}

            {active === "summary" ? (
              <MonthlySummary tasks={tasks} monthDate={monthDate} />
            ) : null}
          </div>
        </main>
      </div>
    </div>
  );
}
