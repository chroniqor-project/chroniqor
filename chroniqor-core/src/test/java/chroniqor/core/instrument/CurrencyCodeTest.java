/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Currency code")
class CurrencyCodeTest {

    @Test
    @DisplayName("accepts a canonical three-letter uppercase code")
    void shouldCreateCanonicalCurrencyCode() {
        CurrencyCode currency = new CurrencyCode("EUR");

        assertEquals("EUR", currency.value());
        assertEquals("EUR", currency.toString());
    }

    @Test
    @DisplayName("rejects a null value")
    void shouldRejectNullValue() {
        assertThrows(NullPointerException.class, () -> new CurrencyCode(null));
    }

    @ParameterizedTest(name = "rejects malformed value: \"{0}\"")
    @ValueSource(strings = {"", "EU", "EURO", "eur", "Eur", " EUR", "EUR ", "12A"})
    void shouldRejectMalformedValues(String value) {
        assertThrows(IllegalArgumentException.class, () -> new CurrencyCode(value));
    }

    @Test
    @DisplayName("uses value-based equality")
    void shouldUseValueBasedEquality() {
        CurrencyCode first = new CurrencyCode("USD");
        CurrencyCode second = new CurrencyCode("USD");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
