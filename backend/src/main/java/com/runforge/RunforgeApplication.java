package com.runforge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RunforgeApplication — Main entry point for the RunForge backend service.
 *
 * <p>This Spring Boot 3.2 application provides a REST API for managing running group
 * members, training plans, and statistics. It uses Spring Data JPA with H2 in-memory
 * database (development) or PostgreSQL (production).
 *
 * <p>Features:
 * <ul>
 *   <li>Member CRUD operations with soft-delete support (active flag)</li>
 *   <li>Training plan management</li>
 *   <li>Plan assignment tracking</li>
 *   <li>Dashboard statistics aggregation</li>
 *   <li>CORS-enabled REST endpoints</li>
 * </ul>
 *
 * <p>Runs on port 8080. H2 console available at http://localhost:8080/h2-console (dev only).
 *
 * @author RunForge Team
 * @version 0.0.1
 */
@SpringBootApplication
public class RunforgeApplication {
    /**
     * Entry point for the RunForge backend application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(RunforgeApplication.class, args);
    }
}
