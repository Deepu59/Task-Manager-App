package com.taskmanager.repository;

import com.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Data-access interface for {@link Task}. Spring Data JPA generates the implementation
 * at runtime — no SQL is written here.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /** All tasks belonging to one user, used to scope every list/read to the logged-in user. */
    List<Task> findByUserId(String userId);
}
