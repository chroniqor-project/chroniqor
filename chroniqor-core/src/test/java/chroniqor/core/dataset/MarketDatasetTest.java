/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.dataset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import chroniqor.core.instrument.CurrencyCode;
import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import chroniqor.core.market.MarketBar;
import chroniqor.core.market.Ohlc;
import chroniqor.core.market.Price;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Market dataset")
class MarketDatasetTest {

    private static final CurrencyPair EUR_USD = new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD"));

    @Test
    @DisplayName("builds identity metadata from the validated bar sequence")
    void shouldBuildIdentityFromBars() {
        MarketBar first = bar("2026-01-01T10:00:00Z", "1.1000");
        MarketBar second = bar("2026-01-01T10:01:00Z", "1.1001");

        MarketDataset dataset = MarketDataset.of("synthetic-eurusd", "1", List.of(first, second));

        assertEquals(EUR_USD, dataset.identity().instrument());
        assertEquals(Timeframe.M1, dataset.identity().timeframe());
        assertEquals(first.startTime(), dataset.identity().startTime());
        assertEquals(second.endTime(), dataset.identity().endTime());
        assertEquals(2, dataset.identity().barCount());
        assertEquals(2, dataset.size());
    }

    @Test
    @DisplayName("rejects bars from different instruments")
    void shouldRejectDifferentInstruments() {
        MarketBar eurUsd = bar("2026-01-01T10:00:00Z", "1.1000");
        MarketBar gbpUsd = new MarketBar(
                new CurrencyPair(new CurrencyCode("GBP"), new CurrencyCode("USD")),
                Timeframe.M1,
                Instant.parse("2026-01-01T10:01:00Z"),
                ohlc("1.2500"),
                ohlc("1.2502"));

        assertThrows(IllegalArgumentException.class, () -> MarketDataset.of("mixed", "1", List.of(eurUsd, gbpUsd)));
    }

    @Test
    @DisplayName("rejects bars from different timeframes")
    void shouldRejectDifferentTimeframes() {
        MarketBar m1 = bar("2026-01-01T10:00:00Z", "1.1000");
        MarketBar m5 = new MarketBar(
                EUR_USD, Timeframe.M5, Instant.parse("2026-01-01T10:05:00Z"), ohlc("1.1000"), ohlc("1.1002"));

        assertThrows(IllegalArgumentException.class, () -> MarketDataset.of("mixed", "1", List.of(m1, m5)));
    }

    @Test
    @DisplayName("rejects an empty dataset")
    void shouldRejectEmptyDataset() {
        assertThrows(IllegalArgumentException.class, () -> MarketDataset.of("empty", "1", List.of()));
    }

    @Test
    @DisplayName("allows gaps without reordering or filling missing bars")
    void shouldAllowGaps() {
        MarketBar first = bar("2026-01-01T10:00:00Z", "1.1000");
        MarketBar afterGap = bar("2026-01-01T10:02:00Z", "1.1002");

        MarketDataset dataset = MarketDataset.of("gapped", "1", List.of(first, afterGap));

        assertEquals(List.of(first, afterGap), dataset.bars());
        assertEquals(2, dataset.identity().barCount());
    }

    private static MarketBar bar(String startTime, String value) {
        return new MarketBar(EUR_USD, Timeframe.M1, Instant.parse(startTime), ohlc(value), ohlc(increment(value)));
    }

    private static Ohlc ohlc(String value) {
        Price price = Price.of(value);
        return new Ohlc(price, price, price, price);
    }

    private static String increment(String value) {
        return new java.math.BigDecimal(value)
                .add(new java.math.BigDecimal("0.0002"))
                .toPlainString();
    }
}
