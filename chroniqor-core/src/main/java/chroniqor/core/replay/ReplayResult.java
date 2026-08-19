/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.replay;

import chroniqor.core.audit.AuditTrail;
import chroniqor.core.dataset.DatasetIdentity;
import chroniqor.core.strategy.StrategyMetadata;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Immutable result of one deterministic strategy replay.
 *
 * @param dataset identity of the replayed dataset
 * @param strategy metadata of the evaluated strategy
 * @param startedAt start time of the first bar
 * @param completedAt availability time of the final bar
 * @param steps ordered per-bar replay outcomes
 * @param auditTrail complete audit stream for the run
 */
public record ReplayResult(
        DatasetIdentity dataset,
        StrategyMetadata strategy,
        Instant startedAt,
        Instant completedAt,
        List<ReplayStep> steps,
        AuditTrail auditTrail) {

    /**
     * Validates temporal, step and audit consistency for a completed replay.
     *
     * @param dataset dataset identity
     * @param strategy strategy metadata
     * @param startedAt replay start time
     * @param completedAt replay completion time
     * @param steps ordered replay steps
     * @param auditTrail complete audit trail
     * @throws IllegalArgumentException if the result is empty or its temporal,
     *     step, or audit invariants do not match
     * @throws NullPointerException if a required value is null
     */
    public ReplayResult {
        Objects.requireNonNull(dataset, "Replay dataset identity must not be null");
        Objects.requireNonNull(strategy, "Replay strategy metadata must not be null");
        Objects.requireNonNull(startedAt, "Replay start time must not be null");
        Objects.requireNonNull(completedAt, "Replay completion time must not be null");
        Objects.requireNonNull(steps, "Replay steps must not be null");
        Objects.requireNonNull(auditTrail, "Replay audit trail must not be null");

        steps = List.copyOf(steps);

        if (steps.isEmpty()) {
            throw new IllegalArgumentException("Replay result must contain at least one step");
        }

        if (!startedAt.equals(steps.getFirst().currentBar().startTime())) {
            throw new IllegalArgumentException("Replay start time must match the first market bar start time");
        }

        if (completedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException("Replay completion time must not be before start time");
        }

        if (!completedAt.equals(steps.getLast().marketTime())) {
            throw new IllegalArgumentException("Replay completion time must match the final replay step");
        }

        if (steps.size() != dataset.barCount()) {
            throw new IllegalArgumentException("Replay step count must match dataset bar count");
        }

        if (!auditTrail.events().getFirst().marketTime().equals(startedAt)) {

            throw new IllegalArgumentException("Audit trail must start at replay start time");
        }
        if (!auditTrail.events().getLast().marketTime().equals(completedAt)) {

            throw new IllegalArgumentException("Audit trail must complete at replay completion time");
        }

        validateSteps(steps);
    }

    /**
     * Returns the number of processed market bars.
     *
     * @return number of replay steps
     */
    public int processedBars() {
        return steps.size();
    }

    private static void validateSteps(List<ReplayStep> steps) {
        for (int i = 0; i < steps.size(); i++) {
            ReplayStep current = steps.get(i);

            if (current.index() != i) {
                throw new IllegalArgumentException("Replay step indexes must be contiguous and zero-based");
            }

            if (i == 0) {
                continue;
            }

            ReplayStep previous = steps.get(i - 1);

            if (!current.marketTime().isAfter(previous.marketTime())) {
                throw new IllegalArgumentException("Replay steps must be strictly ordered by market time");
            }
        }
    }
}
