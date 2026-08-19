/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.instrument;

import java.util.Objects;

/**
 * Ordered currency pair identifying the base and quote currencies.
 *
 * @param base base currency
 * @param quote quote currency
 */
public record CurrencyPair(CurrencyCode base, CurrencyCode quote) {
    /**
     * Validates that base and quote currencies differ.
     *
     * @param base base currency
     * @param quote quote currency
     * @throws IllegalArgumentException if both currencies are equal
     * @throws NullPointerException if either currency is null
     */
    public CurrencyPair {
        Objects.requireNonNull(base, "Base currency must not be null");
        Objects.requireNonNull(quote, "Quote currency must not be null");

        if (base.equals(quote)) {
            throw new IllegalArgumentException("Base and quote currencies must be different");
        }
    }

    /**
     * Returns the conventional slash-separated symbol.
     *
     * @return symbol in the form {@code BASE/QUOTE}
     */
    public String symbol() {
        return base.value() + "/" + quote.value();
    }

    @Override
    /**
     * Returns the pair symbol.
     *
     * @return symbol in the form {@code BASE/QUOTE}
     */
    public String toString() {
        return symbol();
    }
}
