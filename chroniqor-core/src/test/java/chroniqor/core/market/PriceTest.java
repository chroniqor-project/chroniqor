/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.market;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Price")
class PriceTest {

    @Test
    @DisplayName("accepts a positive value")
    void shouldAcceptPositiveValue() {
        Price price = Price.of("1.23450");

        assertEquals(new BigDecimal("1.2345"), price.value());
        assertEquals("1.2345", price.toString());
    }

    @Test
    @DisplayName("rejects a null value")
    void shouldRejectNullValue() {
        assertThrows(NullPointerException.class, () -> new Price(null));
    }

    @ParameterizedTest(name = "rejects non-positive value: {0}")
    @ValueSource(strings = {"0", "-1", "-0.01"})
    void shouldRejectNonPositiveValues(String value) {
        assertThrows(IllegalArgumentException.class, () -> Price.of(value));
    }

    @Test
    @DisplayName("compares values numerically")
    void shouldCompareValuesNumerically() {
        Price lower = Price.of("1.10");
        Price higher = Price.of("1.20");

        assertEquals(-1, lower.compareTo(higher));
        assertEquals(1, higher.compareTo(lower));
        assertEquals(0, lower.compareTo(Price.of("1.100")));
    }

    @Test
    @DisplayName("uses normalized value equality")
    void shouldUseNormalizedValueEquality() {
        Price first = Price.of("100.00");
        Price second = Price.of("100");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
