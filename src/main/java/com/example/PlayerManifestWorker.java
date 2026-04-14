package com.example;

import jakarta.enterprise.context.ApplicationScoped;
import org.jobrunr.jobs.annotations.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Level 3 Worker: Performs the actual heavy lifting for individual items.
 * Each job is independent and will be retried automatically by JobRunr if it fails.
 */
@ApplicationScoped
public class PlayerManifestWorker {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerManifestWorker.class);

    /**
     * Executes the manifest generation for a specific player.
     *
     * @param envId    The ID of the environment the player belongs to.
     * @param playerId The unique identifier of the player.
     * @param batchId  The batch ID for traceability across all levels.
     */
    @Job(name = "[Manifest Generation] | Env: %0 | Player: %1 [Batch: %2]", retries = 3)
    public void generateManifest(Integer envId, String playerId, String batchId) {
        LOG.info("Starting manifest generation for Player: {} (Env: {}) [Batch: {}]", playerId, envId, batchId);

        try {
            // Simulated manifest generation and upload delay (e.g., heavy I/O or API calls)
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            LOG.error("Manifest generation interrupted for Player: {}", playerId);
            Thread.currentThread().interrupt();
            return;
        }

        LOG.info("Successfully completed manifest generation for Player: {} [Batch: {}]", playerId, batchId);
    }
}
