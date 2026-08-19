/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.market;

import java.util.Objects;

/**
 * Immutable OHLC prices satisfying the usual high/low containment
 * invariant.
 *
 * @param open opening price
 * @param high highest price, not below {@code low}
 * @param low lowest price
 * @param close closing price
 */
public record Ohlc(Price open, Price high, Price low, Price close) {

    /**
     * Validates the OHLC containment invariant.
     *
     * @param open opening price
     * @param high high price
     * @param low low price
     * @param close closing price
     * @throws IllegalArgumentException if high is below low, or open/close
     *     falls outside the low-to-high range
     * @throws NullPointerException if a price is null
     */
    public Ohlc {
        Objects.requireNonNull(open, "Open price must not be null");
        Objects.requireNonNull(high, "High price must not be null");
        Objects.requireNonNull(low, "Low price must not be null");
        Objects.requireNonNull(close, "Close price must not be null");

        if (high.compareTo(low) < 0) {
            throw new IllegalArgumentException("High price must be greater than or equal to low price");
        }

        if (open.compareTo(low) < 0 || open.compareTo(high) > 0) {
            throw new IllegalArgumentException("Open price must be between low and high");
        }

        if (close.compareTo(low) < 0 || close.compareTo(high) > 0) {
            throw new IllegalArgumentException("Close price must be between low and high");
        }
    }
}
