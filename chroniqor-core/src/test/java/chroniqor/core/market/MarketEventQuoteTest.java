/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.market;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import chroniqor.core.instrument.CurrencyCode;
import chroniqor.core.instrument.CurrencyPair;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Market quote")
class MarketEventQuoteTest {

    private static final CurrencyPair EUR_USD = new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD"));
    private static final Instant TIME = Instant.parse("2026-01-01T10:00:00Z");
    private static final Price BID = Price.of("1.1000");
    private static final Price ASK = Price.of("1.1002");

    @Test
    @DisplayName("defaults the tick volume to one for a single tick")
    void shouldDefaultTickVolumeToOne() {
        MarketEvent.Quote quote = new MarketEvent.Quote(EUR_USD, TIME, BID, ASK);

        assertEquals(1L, quote.tickVolume());
    }

    @Test
    @DisplayName("rejects null required values")
    void shouldRejectNullValues() {
        assertThrows(NullPointerException.class, () -> new MarketEvent.Quote(null, TIME, BID, ASK));
        assertThrows(NullPointerException.class, () -> new MarketEvent.Quote(EUR_USD, null, BID, ASK));
        assertThrows(NullPointerException.class, () -> new MarketEvent.Quote(EUR_USD, TIME, null, ASK));
        assertThrows(NullPointerException.class, () -> new MarketEvent.Quote(EUR_USD, TIME, BID, null));
    }

    @Test
    @DisplayName("rejects an ask below the bid")
    void shouldRejectAskBelowBid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new MarketEvent.Quote(EUR_USD, TIME, Price.of("1.1002"), Price.of("1.1001")));
    }

    @Test
    @DisplayName("requires positive tick volume")
    void shouldRejectNonPositiveTickVolume() {
        assertThrows(IllegalArgumentException.class, () -> new MarketEvent.Quote(EUR_USD, TIME, BID, ASK, 0));
        assertThrows(IllegalArgumentException.class, () -> new MarketEvent.Quote(EUR_USD, TIME, BID, ASK, -1));
    }
}
