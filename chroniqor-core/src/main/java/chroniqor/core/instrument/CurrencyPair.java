/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.instrument;

import java.util.Objects;

public record CurrencyPair(CurrencyCode base, CurrencyCode quote) {
    public CurrencyPair {
        Objects.requireNonNull(base, "Base currency must not be null");
        Objects.requireNonNull(quote, "Quote currency must not be null");

        if (base.equals(quote)) {
            throw new IllegalArgumentException("Base and quote currencies must be different");
        }
    }

    public String symbol() {
        return base.value() + "/" + quote.value();
    }

    @Override
    public String toString() {
        return symbol();
    }
}
