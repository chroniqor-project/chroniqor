/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.market;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Positive decimal market price with trailing zeros normalized.
 *
 * @param value positive decimal value
 */
public record Price(BigDecimal value) implements Comparable<Price> {

    /**
     * Validates and normalizes the decimal value.
     *
     * @param value positive decimal value
     * @throws IllegalArgumentException if {@code value} is not positive
     * @throws NullPointerException if {@code value} is null
     */
    public Price {
        Objects.requireNonNull(value, "Price must not be null");

        if (value.signum() <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        value = value.stripTrailingZeros();
    }

    /**
     * Parses a decimal price.
     *
     * @param value decimal text accepted by {@link BigDecimal#BigDecimal(String)}
     * @return parsed positive price
     * @throws NumberFormatException if {@code value} is not a valid decimal
     * @throws IllegalArgumentException if the parsed value is not positive
     * @throws NullPointerException if {@code value} is null
     */
    public static Price of(String value) {
        return new Price(new BigDecimal(value));
    }

    @Override
    /**
     * Compares numeric price values, independent of scale.
     *
     * @param other price to compare with
     * @return a negative integer, zero, or a positive integer as this price is
     *     lower than, equal to, or greater than {@code other}
     * @throws NullPointerException if {@code other} is null
     */
    public int compareTo(Price other) {
        Objects.requireNonNull(other, "Price to compare must not be null");
        return value.compareTo(other.value);
    }

    @Override
    /**
     * Returns the normalized decimal without scientific notation.
     *
     * @return plain-text price
     */
    public String toString() {
        return value.toPlainString();
    }
}
