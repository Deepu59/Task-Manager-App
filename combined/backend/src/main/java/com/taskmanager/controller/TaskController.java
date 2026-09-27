package com.taskmanager.controller;

import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for creating, reading, updating, and deleting {@link Task}s.
 * Base path: {@code /tasks}. Every list/read is scoped to a {@code userId} query
 * parameter (a browser-local workspace ID) to separate task lists.
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /** GET /tasks?userId=... — all tasks belonging to that user. */
    @GetMapping
    public List<Task> getTasks(@RequestParam String userId) {
        return taskRepository.findByUserId(userId);
    }

    /**
     * POST /tasks — creates a task. {@code @Valid} enforces userId/title/date being present;
     * failures are turned into a 400 by {@code GlobalExceptionHandler}.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody Task task) {
        long now = System.currentTimeMillis();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        return taskRepository.save(task);
    }

    /**
     * PUT /tasks/{id} — replaces an existing task's fields.
     * @throws ResourceNotFoundException resulting in a 404 if no task has that id
     */
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @Valid @RequestBody Task updatedTask) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));

        existing.setTitle(updatedTask.getTitle());
        existing.setNotes(updatedTask.getNotes());
        existing.setDate(updatedTask.getDate());
        existing.setStart(updatedTask.getStart());
        existing.setEnd(updatedTask.getEnd());
        existing.setCategory(updatedTask.getCategory());
        existing.setPriority(updatedTask.getPriority());
        existing.setDone(updatedTask.isDone());
        existing.setUpdatedAt(System.currentTimeMillis());

        return taskRepository.save(existing);
    }

    /**
     * PATCH /tasks/{id}/done — toggles just the done flag, for the checkbox in TaskPanel.
     * @throws ResourceNotFoundException resulting in a 404 if no task has that id
     */
    @PatchMapping("/{id}/done")
    public Task toggleDone(@PathVariable Long id) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
        existing.setDone(!existing.isDone());
        existing.setUpdatedAt(System.currentTimeMillis());
        return taskRepository.save(existing);
    }

    /**
     * DELETE /tasks/{id} — removes a task.
     * @throws ResourceNotFoundException resulting in a 404 if no task has that id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id " + id);
        }
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
