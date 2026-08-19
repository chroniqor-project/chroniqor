/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.replay;

import chroniqor.core.market.MarketBar;
import chroniqor.core.strategy.StrategyDecision;
import java.time.Instant;
import java.util.Objects;

/**
 * Immutable outcome of evaluating a strategy at one replay position.
 *
 * @param index zero-based dataset index
 * @param marketTime explicit time at which the bar is available
 * @param currentBar bar visible at this step
 * @param decision strategy decision for the visible history
 */
public record ReplayStep(int index, Instant marketTime, MarketBar currentBar, StrategyDecision decision) {

    /**
     * Validates that the step's explicit time matches bar availability.
     *
     * @param index zero-based dataset index
     * @param marketTime step market time
     * @param currentBar visible bar
     * @param decision strategy decision
     * @throws IllegalArgumentException if the index is negative or market time
     *     differs from the bar availability time
     * @throws NullPointerException if a required value is null
     */
    public ReplayStep {
        if (index < 0) {
            throw new IllegalArgumentException("Replay step index must not be negative");
        }

        Objects.requireNonNull(marketTime, "Replay step market time must not be null");

        Objects.requireNonNull(currentBar, "Replay step current bar must not be null");

        Objects.requireNonNull(decision, "Replay step strategy decision must not be null");

        if (!marketTime.equals(currentBar.availableAt())) {
            throw new IllegalArgumentException("Replay step market time must match the current bar availability time");
        }
    }
}
