package com.example;

import com.example.database.EnvironmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Level 1 Orchestrator: Entry point for the batch manifest generation process.
 * Responsible for scheduling the recurring job and initiating the fan-out across environments.
 */
@ApplicationScoped
public class ManifestBatchOrchestrator {

    private static final Logger LOG = LoggerFactory.getLogger(ManifestBatchOrchestrator.class);
    private static final String RECURRENT_JOB_ID = "manifest-batch-orchestrator";
    private static final String CRON_EXPRESSION = "*/5 * * * *"; // Runs every 5 minutes
    private static final DateTimeFormatter BATCH_ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HH'H'mm");

    @Inject
    JobScheduler jobScheduler;

    @Inject
    EnvironmentRepository environmentRepository;

    @Inject
    EnvironmentManifestProcessor environmentManifestProcessor;

    /**
     * Initializes the system by scheduling the recurring orchestration job.
     * Triggered on application startup.
     */
    public void onStart(@Observes StartupEvent event) {
        LOG.info("Initializing Manifest Batch Orchestrator with schedule: {}", CRON_EXPRESSION);
        jobScheduler.scheduleRecurrently(RECURRENT_JOB_ID, CRON_EXPRESSION,
                this::orchestrateBatch);
    }

    /**
     * Executes the orchestration logic to start a new generation batch.
     * Identifies active environments and enqueues processing jobs for Level 2.
     */
    @Job(name = "[Manifest Generation] | Batch Orchestrator | Starting a new batch")
    public void orchestrateBatch() {
        String batchId = "BATCH-" + LocalDateTime.now().format(BATCH_ID_FORMATTER);
        LOG.info("Starting a new manifest generation batch: {}", batchId);

        List<Integer> activeEnvironments = environmentRepository.getActiveEnvironments();
        LOG.info("Identified {} active environments for batch processing.", activeEnvironments.size());

        for (Integer envId : activeEnvironments) {
            LOG.debug("Enqueuing environment manifest processing tasks for Environment Id: {}", envId);
            jobScheduler.enqueue(() ->
                    environmentManifestProcessor.process(envId, batchId)
            );
        }

        LOG.info("Successfully initiated processing for {} environments [Batch: {}]", activeEnvironments.size(), batchId);
    }
}
