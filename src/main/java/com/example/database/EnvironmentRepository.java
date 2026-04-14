package com.example.database;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class EnvironmentRepository {

    /**
     * Simulates fetching active BUs/environments from a data source.
     * In a real application, this would probably query a database.
     *
     * @return A list of active environment IDs.
     */
    public List<Integer> getActiveEnvironments() {
        return List.of(0, 1, 2);
    }

    /**
     * Simulates searching for players (devices) within a specific environment.
     *
     * @param envId The environment identifier.
     * @return A list of player unique identifiers.
     */
    public List<String> getPlayersForEnvironment(Integer envId) {
        Map<Integer, List<String>> playersByEnvironmentId = Map.of(
                0, List.of("Player-FR-001", "Player-FR-002", "Player-FR-003"),
                1, List.of("Player-UK-101", "Player-UK-102"),
                2, List.of("Player-ES-201", "Player-ES-202", "Player-ES-203", "Player-ES-204")
        );

        return playersByEnvironmentId.getOrDefault(envId, List.of());
    }
}