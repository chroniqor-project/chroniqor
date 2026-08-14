/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.replay;

import chroniqor.core.dataset.DatasetIdentity;
import chroniqor.core.strategy.StrategyMetadata;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record ReplayResult(
        DatasetIdentity dataset,
        StrategyMetadata strategy,
        Instant startedAt,
        Instant completedAt,
        List<ReplayStep> steps) {

    public ReplayResult {
        Objects.requireNonNull(dataset, "Replay dataset identity must not be null");
        Objects.requireNonNull(strategy, "Replay strategy metadata must not be null");
        Objects.requireNonNull(startedAt, "Replay start time must not be null");
        Objects.requireNonNull(completedAt, "Replay completion time must not be null");
        Objects.requireNonNull(steps, "Replay steps must not be null");

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

        validateSteps(steps);
    }

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
