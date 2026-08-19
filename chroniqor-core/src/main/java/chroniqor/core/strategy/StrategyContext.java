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

/**
 * Immutable strategy view of the market history visible at one replay step.
 *
 * @param history non-empty prefix of the dataset visible to the strategy
 */
public record StrategyContext(MarketHistory history) {

    /**
     * Validates the visible history.
     *
     * @param history non-empty visible market history
     * @throws NullPointerException if {@code history} is null
     */
    public StrategyContext {
        Objects.requireNonNull(history, "Market history must not be null");
    }

    /**
     * Returns the latest visible bar.
     *
     * @return current visible bar
     */
    public MarketBar currentBar() {
        return history.current();
    }

    /**
     * Returns the explicit availability time of the current bar.
     *
     * @return current market time
     */
    public Instant marketTime() {
        return currentBar().availableAt();
    }

    /**
     * Returns the current instrument.
     *
     * @return current bar instrument
     */
    public CurrencyPair instrument() {
        return currentBar().instrument();
    }

    /**
     * Returns the current bar timeframe.
     *
     * @return current bar timeframe
     */
    public Timeframe timeframe() {
        return currentBar().timeframe();
    }
}
