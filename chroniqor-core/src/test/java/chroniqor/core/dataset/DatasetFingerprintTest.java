/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.dataset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import chroniqor.core.instrument.CurrencyCode;
import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import chroniqor.core.market.MarketBar;
import chroniqor.core.market.MarketEvent;
import chroniqor.core.market.Ohlc;
import chroniqor.core.market.Price;
import chroniqor.core.testing.SyntheticMarketData;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Dataset fingerprint")
class DatasetFingerprintTest {

    private static final CurrencyPair EUR_USD = new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD"));

    @Test
    @DisplayName("generates a deterministic lowercase SHA-256 hash from the dataset content")
    void shouldGenerateDeterministicHash() {
        MarketDataset first = dataset(List.of(bar("2026-01-01T10:00:00Z", "1.1000", 10)));
        MarketDataset second = dataset(List.of(bar("2026-01-01T10:00:00Z", "1.1000", 10)));

        String hash = first.identity().contentHash();

        assertEquals(hash, second.identity().contentHash());
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    @Test
    @DisplayName("matches the frozen V1 golden fingerprint")
    void shouldMatchDatasetV1GoldenFingerprint() {
        MarketDataset dataset = MarketDataset.of("synthetic-eurusd", "1", SyntheticMarketData.bars());

        assertEquals(
                "56ba37cfc2f906c0f0836d731370ecad29c124c30327465f03bbbd903115cd32",
                dataset.identity().contentHash());
    }

    @Test
    @DisplayName("normalizes equivalent decimal scales in the fingerprint")
    void shouldIgnoreEquivalentDecimalScale() {
        MarketDataset first = dataset(List.of(bar("2026-01-01T10:00:00Z", "1.1000", 10)));
        MarketDataset second = dataset(List.of(bar("2026-01-01T10:00:00Z", "1.1", 10)));

        assertEquals(first.identity().contentHash(), second.identity().contentHash());
    }

    @Test
    @DisplayName("changes when tick volume changes")
    void shouldChangeHashWhenTickVolumeChanges() {
        MarketDataset first = dataset(List.of(bar("2026-01-01T10:00:00Z", "1.1000", 10)));
        MarketDataset second = dataset(List.of(bar("2026-01-01T10:00:00Z", "1.1000", 11)));

        assertFalse(first.identity().contentHash().equals(second.identity().contentHash()));
    }

    @Test
    @DisplayName("changes when the bar timestamp changes")
    void shouldChangeHashWhenTimestampChanges() {
        MarketDataset first = dataset(List.of(bar("2026-01-01T10:00:00Z", "1.1000", 10)));
        MarketDataset second = dataset(List.of(bar("2026-01-01T10:01:00Z", "1.1000", 10)));

        assertFalse(first.identity().contentHash().equals(second.identity().contentHash()));
    }

    @Test
    @DisplayName("changes when the instrument changes")
    void shouldChangeHashWhenInstrumentChanges() {
        MarketDataset first = dataset(List.of(bar("2026-01-01T10:00:00Z", "1.1000", 10)));
        CurrencyPair gbpUsd = new CurrencyPair(new CurrencyCode("GBP"), new CurrencyCode("USD"));
        MarketBar gbpBar = bar(gbpUsd, Timeframe.M1, "2026-01-01T10:00:00Z", "1.2500", 10);
        MarketDataset second = MarketDataset.of("synthetic-gbpusd", "1", List.of(gbpBar));

        assertFalse(first.identity().contentHash().equals(second.identity().contentHash()));
    }

    @Test
    @DisplayName("changes when the timeframe changes")
    void shouldChangeHashWhenTimeframeChanges() {
        MarketDataset first = dataset(List.of(bar("2026-01-01T10:00:00Z", "1.1000", 10)));
        MarketBar m5Bar = bar(EUR_USD, Timeframe.M5, "2026-01-01T10:00:00Z", "1.1000", 10);
        MarketDataset second = MarketDataset.of("synthetic-eurusd-m5", "1", List.of(m5Bar));

        assertFalse(first.identity().contentHash().equals(second.identity().contentHash()));
    }

    @Test
    @DisplayName("changes the hash when an ordered bar value changes")
    void shouldChangeHashWhenContentChanges() {
        MarketDataset first = dataset(List.of(bar("2026-01-01T10:00:00Z", "1.1000", 10)));
        MarketDataset second = dataset(List.of(bar("2026-01-01T10:00:00Z", "1.1001", 10)));

        assertFalse(first.identity().contentHash().equals(second.identity().contentHash()));
    }

    @Test
    @DisplayName("keeps the input sequence and rejects duplicated timestamps")
    void shouldRejectDuplicatedTimestamps() {
        MarketBar first = bar("2026-01-01T10:00:00Z", "1.1000", 10);
        MarketBar duplicate = bar("2026-01-01T10:00:00Z", "1.1001", 20);

        assertThrows(IllegalArgumentException.class, () -> dataset(List.of(first, duplicate)));
    }

    @Test
    @DisplayName("rejects an out-of-order sequence instead of reordering it silently")
    void shouldRejectOutOfOrderBars() {
        MarketBar first = bar("2026-01-01T10:00:00Z", "1.1000", 10);
        MarketBar second = bar("2026-01-01T10:01:00Z", "1.1001", 20);

        assertThrows(IllegalArgumentException.class, () -> dataset(List.of(second, first)));
    }

    @Test
    @DisplayName("copies the input list and exposes an unmodifiable sequence")
    void shouldKeepDatasetImmutable() {
        List<MarketBar> source = new ArrayList<>();
        source.add(bar("2026-01-01T10:00:00Z", "1.1000", 10));

        MarketDataset dataset = dataset(source);
        source.clear();

        assertEquals(1, dataset.size());
        assertThrows(UnsupportedOperationException.class, () -> dataset.bars().clear());
    }

    @Test
    @DisplayName("uses final fields for the dataset state")
    void shouldUseFinalFields() throws NoSuchFieldException {
        assertTrue(Modifier.isFinal(DatasetFingerprint.class.getModifiers()));
        assertTrue(Modifier.isFinal(Price.class.getModifiers()));
        assertTrue(Modifier.isFinal(Ohlc.class.getModifiers()));
        assertTrue(Modifier.isFinal(MarketBar.class.getModifiers()));
        assertTrue(Modifier.isFinal(MarketEvent.Quote.class.getModifiers()));
        assertTrue(Modifier.isFinal(
                MarketDataset.class.getDeclaredField("identity").getModifiers()));
        assertTrue(Modifier.isFinal(MarketDataset.class.getDeclaredField("bars").getModifiers()));
        assertTrue(Modifier.isFinal(
                DatasetIdentity.class.getDeclaredField("datasetId").getModifiers()));
        assertTrue(Modifier.isFinal(
                DatasetIdentity.class.getDeclaredField("contentHash").getModifiers()));
    }

    @Test
    @DisplayName("rejects an identity whose metadata or hash does not match the bars")
    void shouldRejectInconsistentIdentity() {
        MarketBar bar = bar("2026-01-01T10:00:00Z", "1.1000", 10);
        MarketDataset valid = dataset(List.of(bar));
        DatasetIdentity invalidIdentity = new DatasetIdentity(
                valid.identity().datasetId(),
                valid.identity().version(),
                EUR_USD,
                valid.identity().timeframe(),
                valid.identity().startTime(),
                valid.identity().endTime(),
                valid.identity().barCount(),
                "0000000000000000000000000000000000000000000000000000000000000000");

        assertThrows(IllegalArgumentException.class, () -> newDataset(invalidIdentity, List.of(bar)));
    }

    private static MarketDataset dataset(List<MarketBar> bars) {
        return MarketDataset.of("synthetic-eurusd", "1", bars);
    }

    private static MarketDataset newDataset(DatasetIdentity identity, List<MarketBar> bars) {
        return MarketDataset.fromIdentity(identity, bars);
    }

    private static MarketBar bar(String startTime, String bidOpen, long tickVolume) {
        return bar(EUR_USD, Timeframe.M1, startTime, bidOpen, tickVolume);
    }

    private static MarketBar bar(
            CurrencyPair instrument, Timeframe timeframe, String startTime, String bidOpen, long tickVolume) {
        Price bid = Price.of(bidOpen);
        Price ask = new Price(new java.math.BigDecimal(bidOpen).add(new java.math.BigDecimal("0.0002")));
        Ohlc bidOhlc = new Ohlc(bid, bid, bid, bid);
        Ohlc askOhlc = new Ohlc(ask, ask, ask, ask);

        return new MarketBar(instrument, timeframe, Instant.parse(startTime), bidOhlc, askOhlc, tickVolume);
    }
}
