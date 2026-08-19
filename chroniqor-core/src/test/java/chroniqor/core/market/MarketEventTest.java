/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.market;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import chroniqor.core.instrument.CurrencyCode;
import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Market event")
class MarketEventTest {

    @Test
    @DisplayName("market bars expose their instrument and availability time")
    void shouldExposeMarketEventMetadata() {
        Instant start = Instant.parse("2026-01-01T10:00:00Z");
        MarketBar bar = new MarketBar(
                new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD")),
                Timeframe.M1,
                start,
                ohlc("1.1000", "1.1100", "1.0900", "1.1050"),
                ohlc("1.1002", "1.1102", "1.0902", "1.1052"));

        MarketEvent event = bar;

        assertInstanceOf(MarketBar.class, event);
        assertEquals(bar.instrument(), event.instrument());
        assertEquals(bar.endTime(), event.availableAt());
    }

    private static Ohlc ohlc(String open, String high, String low, String close) {
        return new Ohlc(Price.of(open), Price.of(high), Price.of(low), Price.of(close));
    }
}
