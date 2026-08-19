/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.instrument;

import java.util.Objects;

/**
 * Three-letter uppercase ISO-style currency code used by an instrument.
 *
 * @param value exactly three uppercase ASCII letters
 */
public record CurrencyCode(String value) {

    /**
     * Validates the three-letter currency code.
     *
     * @param value currency code
     * @throws IllegalArgumentException if {@code value} is not three uppercase
     *     ASCII letters
     * @throws NullPointerException if {@code value} is null
     */
    public CurrencyCode {
        Objects.requireNonNull(value, "Currency code must not be null");

        if (!value.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Currency code must contain exactly three uppercase ASCII letters");
        }
    }

    @Override
    /**
     * Returns the code text.
     *
     * @return uppercase currency code
     */
    public String toString() {
        return value;
    }
}
