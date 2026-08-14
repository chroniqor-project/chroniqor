/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.replay;

import chroniqor.core.market.MarketBar;
import chroniqor.core.strategy.StrategyDecision;
import java.time.Instant;
import java.util.Objects;

public record ReplayStep(int index, Instant marketTime, MarketBar currentBar, StrategyDecision decision) {

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
