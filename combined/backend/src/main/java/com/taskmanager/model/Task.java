package com.taskmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single task on a user's schedule. Maps to the {@code tasks} table.
 *
 * <p>Shape matches what the React dashboard sends/expects: a title, optional notes,
 * a calendar date, optional start/end time strings, a category and priority label,
 * a done flag, and the browser-local workspace ID used to group it.
 *
 * <p>{@code @Data} (Lombok) generates getters, setters, {@code equals()}, {@code hashCode()},
 * and {@code toString()} at compile time.
 */
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Browser-local workspace ID used to scope task queries. This is not auth.
    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "Title is required")
    private String title;

    @Column(length = 2000)
    private String notes;

    // Stored as "yyyy-MM-dd" text to match date-fns formatting used on the frontend.
    @NotBlank(message = "date is required")
    private String date;

    // "HH:mm" strings, e.g. "09:30". Optional — a task can have no scheduled time.
    private String start;

    @Column(name = "end_time")
    private String end;

    // Free-form to match the frontend's fixed set (study/teaching/gym/personal)
    // without forcing a backend redeploy if the frontend adds a new category.
    private String category;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.medium;

    private boolean done = false;

    private Long createdAt;
    private Long updatedAt;

    public enum Priority {
        high, medium, low
    }
}
