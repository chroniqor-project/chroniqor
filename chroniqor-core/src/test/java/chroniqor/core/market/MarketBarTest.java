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
import java.util.List;
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
    @DisplayName("aggregates ordered quote events into a bar")
    void shouldAggregateQuoteEvents() {
        MarketEvent.Quote first = quote("2026-01-01T10:00:05Z", "1.1000", "1.1002", 100);
        MarketEvent.Quote second = quote("2026-01-01T10:00:30Z", "1.1010", "1.1012", 200);

        MarketBar bar = MarketBar.fromQuotes(List.of(first, second), Timeframe.M1);

        assertEquals(Price.of("1.1000"), bar.bid().open());
        assertEquals(Price.of("1.1010"), bar.bid().high());
        assertEquals(Price.of("1.1000"), bar.bid().low());
        assertEquals(Price.of("1.1010"), bar.bid().close());
        assertEquals(300, bar.tickVolume());
    }

    @Test
    @DisplayName("rejects quote events outside the target interval")
    void shouldRejectEventsOutsideInterval() {
        MarketEvent.Quote first = quote("2026-01-01T10:00:05Z", "1.1000", "1.1002", 100);
        MarketEvent.Quote nextMinute = quote("2026-01-01T10:01:00Z", "1.1010", "1.1012", 200);

        assertThrows(
                IllegalArgumentException.class, () -> MarketBar.fromQuotes(List.of(first, nextMinute), Timeframe.M1));
    }

    @Test
    @DisplayName("rejects events that are not strictly ordered")
    void shouldRejectUnorderedEvents() {
        MarketEvent.Quote first = quote("2026-01-01T10:00:30Z", "1.1000", "1.1002", 100);
        MarketEvent.Quote second = quote("2026-01-01T10:00:05Z", "1.1010", "1.1012", 200);

        assertThrows(IllegalArgumentException.class, () -> MarketBar.fromQuotes(List.of(first, second), Timeframe.M1));
    }

    @Test
    @DisplayName("rejects quote events with duplicated timestamps")
    void shouldRejectDuplicatedEventTimes() {
        MarketEvent.Quote first = quote("2026-01-01T10:00:05Z", "1.1000", "1.1002", 100);
        MarketEvent.Quote duplicate = quote("2026-01-01T10:00:05Z", "1.1001", "1.1003", 200);

        assertThrows(
                IllegalArgumentException.class, () -> MarketBar.fromQuotes(List.of(first, duplicate), Timeframe.M1));
    }

    @Test
    @DisplayName("rejects quote events from different instruments")
    void shouldRejectDifferentQuoteInstruments() {
        CurrencyPair gbpUsd = new CurrencyPair(new CurrencyCode("GBP"), new CurrencyCode("USD"));
        MarketEvent.Quote first = quote("2026-01-01T10:00:05Z", "1.1000", "1.1002", 100);
        MarketEvent.Quote differentInstrument = new MarketEvent.Quote(
                gbpUsd, Instant.parse("2026-01-01T10:00:06Z"), Price.of("1.2500"), Price.of("1.2502"), 200);

        assertThrows(
                IllegalArgumentException.class,
                () -> MarketBar.fromQuotes(List.of(first, differentInstrument), Timeframe.M1));
    }

    @Test
    @DisplayName("assigns the last representable instant to the current bucket")
    void shouldKeepNanosecondBeforeBoundaryInCurrentBucket() {
        MarketEvent.Quote quote = quote("2026-01-01T10:00:59.999999999Z", "1.1000", "1.1002", 1);

        MarketBar bar = MarketBar.fromQuotes(List.of(quote), Timeframe.M1);

        assertEquals(Instant.parse("2026-01-01T10:00:00Z"), bar.startTime());
    }

    @Test
    @DisplayName("rejects a negative bar tick volume")
    void shouldRejectNegativeTickVolume() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new MarketBar(
                        EUR_USD,
                        Timeframe.M1,
                        START,
                        ohlc("1.1000", "1.1000", "1.1000", "1.1000"),
                        ohlc("1.1002", "1.1002", "1.1002", "1.1002"),
                        -1));
    }

    @Test
    @DisplayName("requires the bar start time to be aligned to the timeframe")
    void shouldRequireAlignedStartTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new MarketBar(
                        EUR_USD,
                        Timeframe.M5,
                        START.plusNanos(1),
                        ohlc("1.1000", "1.1000", "1.1000", "1.1000"),
                        ohlc("1.1002", "1.1002", "1.1002", "1.1002")));
    }

    @Test
    @DisplayName("rejects tick-volume overflow during aggregation")
    void shouldRejectTickVolumeOverflow() {
        MarketEvent.Quote first = quote("2026-01-01T10:00:05Z", "1.1000", "1.1002", Long.MAX_VALUE);
        MarketEvent.Quote second = quote("2026-01-01T10:00:06Z", "1.1001", "1.1003", 1);

        assertThrows(ArithmeticException.class, () -> MarketBar.fromQuotes(List.of(first, second), Timeframe.M1));
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

    private static MarketEvent.Quote quote(String time, String bid, String ask, long tickVolume) {
        return new MarketEvent.Quote(EUR_USD, Instant.parse(time), Price.of(bid), Price.of(ask), tickVolume);
    }
}
