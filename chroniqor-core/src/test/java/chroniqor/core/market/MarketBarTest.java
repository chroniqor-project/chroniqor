/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.market;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import chroniqor.core.instrument.CurrencyCode;
import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Market bar")
class MarketBarTest {

    private static final CurrencyPair EUR_USD = new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD"));

    private static final Instant START = Instant.parse("2026-01-01T10:00:00Z");

    @Test
    @DisplayName("accepts a valid bid-ask bar")
    void shouldCreateValidMarketBar() {
        MarketBar bar = newMarketBar(
                ohlc("1.1000", "1.1100", "1.0900", "1.1050"), ohlc("1.1002", "1.1102", "1.0902", "1.1052"));

        assertEquals(EUR_USD, bar.instrument());
        assertEquals(Timeframe.M5, bar.timeframe());
        assertEquals(START, bar.startTime());
    }

    @Test
    @DisplayName("calculates the end time from the timeframe")
    void shouldCalculateEndTime() {
        MarketBar bar = newMarketBar(
                ohlc("1.1000", "1.1100", "1.0900", "1.1050"), ohlc("1.1002", "1.1102", "1.0902", "1.1052"));

        assertEquals(Instant.parse("2026-01-01T10:05:00Z"), bar.endTime());
        assertEquals(bar.endTime(), bar.availableAt());
    }

    @Test
    @DisplayName("rejects an ask price below the bid price")
    void shouldRejectAskBelowBid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> newMarketBar(
                        ohlc("1.1000", "1.1100", "1.0900", "1.1050"), ohlc("1.0999", "1.1102", "1.0902", "1.1052")));
    }

    @Test
    @DisplayName("rejects null bar components")
    void shouldRejectNullComponents() {
        assertThrows(
                NullPointerException.class,
                () -> new MarketBar(EUR_USD, Timeframe.M5, START, null, ohlc("1.1002", "1.1102", "1.0902", "1.1052")));
        assertThrows(
                NullPointerException.class,
                () -> new MarketBar(EUR_USD, Timeframe.M5, START, ohlc("1.1000", "1.1100", "1.0900", "1.1050"), null));
    }

    private static MarketBar newMarketBar(Ohlc bid, Ohlc ask) {
        return new MarketBar(EUR_USD, Timeframe.M5, START, bid, ask);
    }

    private static Ohlc ohlc(String open, String high, String low, String close) {
        return new Ohlc(Price.of(open), Price.of(high), Price.of(low), Price.of(close));
    }
}
