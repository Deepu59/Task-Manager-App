package com.taskmanager.exception;

/**
 * Thrown when a requested entity (a task or category id) doesn't exist.
 * Caught by {@link GlobalExceptionHandler} and converted into an HTTP 404 response —
 * controllers just {@code throw} it and don't build the response themselves.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
