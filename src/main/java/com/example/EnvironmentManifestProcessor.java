package com.example;

import com.example.database.EnvironmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.jobs.annotations.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Level 2 Processor: Handles the fan-out for a specific environment.
 * Responsible for finding all targets (players) and enqueuing their individual jobs.
 */
@ApplicationScoped
public class EnvironmentManifestProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentManifestProcessor.class);

    @Inject
    JobScheduler jobScheduler;

    @Inject
    EnvironmentRepository repository;

    @Inject
    PlayerManifestWorker playerWorker;

    /**
     * Processes the generation for an entire environment.
     * Fetches the list of players and delegates work to Level 3.
     *
     * @param envId   The ID of the environment to process.
     * @param batchId The batch ID for traceability.
     */
    @Job(name = "[Manifest Generation] | Environment Processing | Env: %0 [Batch: %1]")
    public void process(Integer envId, String batchId) {
        LOG.info("Processing manifest generation for Environment {}. Searching for players...", envId);

        List<String> players = repository.getPlayersForEnvironment(envId);
        LOG.info("Found {} players for Environment {}. Enqueuing worker tasks.", players.size(), envId);

        for (String playerId : players) {
            jobScheduler.enqueue(() ->
                    playerWorker.generateManifest(envId, playerId, batchId)
            );
        }

        LOG.info("Successfully enqueued {} worker jobs for Environment {} [Batch: {}]", players.size(), envId, batchId);
    }
}
