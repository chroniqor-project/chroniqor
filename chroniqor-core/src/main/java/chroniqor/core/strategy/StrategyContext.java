/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import chroniqor.core.market.MarketBar;
import java.time.Instant;
import java.util.Objects;

public record StrategyContext(MarketHistory history) {

    public StrategyContext {
        Objects.requireNonNull(history, "Market history must not be null");
    }

    public MarketBar currentBar() {
        return history.current();
    }

    public Instant marketTime() {
        return currentBar().availableAt();
    }

    public CurrencyPair instrument() {
        return currentBar().instrument();
    }

    public Timeframe timeframe() {
        return currentBar().timeframe();
    }
}
