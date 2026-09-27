package com.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Task Manager REST API.
 *
 * <p>{@code @SpringBootApplication} bundles three annotations:
 * <ul>
 *   <li>{@code @Configuration} — this class can define Spring beans</li>
 *   <li>{@code @EnableAutoConfiguration} — Spring Boot wires up beans (a web server,
 *       a JPA {@code EntityManager}, etc.) based on the dependencies on the classpath</li>
 *   <li>{@code @ComponentScan} — Spring scans this package and sub-packages for
 *       {@code @RestController}, {@code @Repository}, and similar annotated classes</li>
 * </ul>
 */
@SpringBootApplication
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}
