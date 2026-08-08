/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Currency pair")
class CurrencyPairTest {

    @Test
    @DisplayName("creates a valid currency pair")
    void shouldCreateCurrencyPair() {
        CurrencyPair pair = new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD"));

        assertEquals(new CurrencyCode("EUR"), pair.base());
        assertEquals(new CurrencyCode("USD"), pair.quote());
        assertEquals("EUR/USD", pair.symbol());
        assertEquals("EUR/USD", pair.toString());
    }

    @Test
    @DisplayName("rejects a null base currency")
    void shouldRejectNullBaseCurrency() {
        assertThrows(NullPointerException.class, () -> new CurrencyPair(null, new CurrencyCode("USD")));
    }

    @Test
    @DisplayName("rejects a null quote currency")
    void shouldRejectNullQuoteCurrency() {
        assertThrows(NullPointerException.class, () -> new CurrencyPair(new CurrencyCode("EUR"), null));
    }

    @Test
    @DisplayName("rejects identical base and quote currencies")
    void shouldRejectIdenticalCurrencies() {
        CurrencyCode eur = new CurrencyCode("EUR");

        assertThrows(IllegalArgumentException.class, () -> new CurrencyPair(eur, eur));
    }

    @Test
    @DisplayName("uses value-based equality")
    void shouldUseValueBasedEquality() {
        CurrencyPair first = new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD"));

        CurrencyPair second = new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD"));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
