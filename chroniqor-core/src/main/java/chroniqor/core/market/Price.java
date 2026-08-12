/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.market;

import java.math.BigDecimal;
import java.util.Objects;

public record Price(BigDecimal value) implements Comparable<Price> {

    public Price {
        Objects.requireNonNull(value, "Price must not be null");

        if (value.signum() <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        value = value.stripTrailingZeros();
    }

    public static Price of(String value) {
        return new Price(new BigDecimal(value));
    }

    @Override
    public int compareTo(Price other) {
        Objects.requireNonNull(other, "Price to compare must not be null");
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}
