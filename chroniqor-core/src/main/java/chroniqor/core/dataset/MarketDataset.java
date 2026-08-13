/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.dataset;

import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import chroniqor.core.market.MarketBar;
import java.util.List;
import java.util.Objects;

public final class MarketDataset {

    private final DatasetIdentity identity;
    private final List<MarketBar> bars;

    private MarketDataset(DatasetIdentity identity, List<MarketBar> bars) {
        this.identity = Objects.requireNonNull(identity, "Dataset identity must not be null");
        this.bars = List.copyOf(Objects.requireNonNull(bars, "Market bars must not be null"));

        if (this.bars.isEmpty()) {
            throw new IllegalArgumentException("Market dataset must contain at least one bar");
        }

        validateBars(this.bars);
        validateIdentity(this.identity, this.bars);
    }

    public static MarketDataset of(String datasetId, String version, List<MarketBar> bars) {
        Objects.requireNonNull(bars, "Market bars must not be null");

        if (bars.isEmpty()) {
            throw new IllegalArgumentException("Market dataset must contain at least one bar");
        }

        List<MarketBar> immutableBars = List.copyOf(bars);

        validateBars(immutableBars);

        MarketBar first = immutableBars.getFirst();
        MarketBar last = immutableBars.getLast();

        CurrencyPair instrument = first.instrument();
        Timeframe timeframe = first.timeframe();

        String contentHash = DatasetFingerprint.sha256(instrument, timeframe, immutableBars);

        DatasetIdentity identity = new DatasetIdentity(
                datasetId,
                version,
                instrument,
                timeframe,
                first.startTime(),
                last.endTime(),
                immutableBars.size(),
                contentHash);

        return new MarketDataset(identity, immutableBars);
    }

    static MarketDataset fromIdentity(DatasetIdentity identity, List<MarketBar> bars) {
        return new MarketDataset(identity, bars);
    }

    public DatasetIdentity identity() {
        return identity;
    }

    public List<MarketBar> bars() {
        return bars;
    }

    public int size() {
        return bars.size();
    }

    private static void validateBars(List<MarketBar> bars) {

        MarketBar first = bars.getFirst();
        CurrencyPair expectedInstrument = first.instrument();
        Timeframe expectedTimeframe = first.timeframe();

        for (int i = 0; i < bars.size(); i++) {
            MarketBar current = bars.get(i);

            if (!current.instrument().equals(expectedInstrument)) {
                throw new IllegalArgumentException("All market bars must belong to the same instrument");
            }

            if (current.timeframe() != expectedTimeframe) {
                throw new IllegalArgumentException("All market bars must use the same timeframe");
            }

            if (i == 0) {
                continue;
            }

            MarketBar previous = bars.get(i - 1);

            validateChronologicalOrder(previous, current);
        }
    }

    private static void validateChronologicalOrder(MarketBar previous, MarketBar current) {
        if (current.startTime().equals(previous.startTime())) {
            throw new IllegalArgumentException("Market dataset contains duplicated bar timestamps");
        }

        if (current.startTime().isBefore(previous.startTime())) {
            throw new IllegalArgumentException("Market bars must be strictly ordered by market time");
        }

        if (current.startTime().isBefore(previous.endTime())) {
            throw new IllegalArgumentException("Market bars must not overlap");
        }
    }

    private static void validateIdentity(DatasetIdentity identity, List<MarketBar> bars) {
        MarketBar first = bars.getFirst();
        MarketBar last = bars.getLast();

        if (!identity.instrument().equals(first.instrument())) {
            throw new IllegalArgumentException("Dataset identity instrument does not match its bars");
        }

        if (identity.timeframe() != first.timeframe()) {
            throw new IllegalArgumentException("Dataset identity timeframe does not match its bars");
        }

        if (!identity.startTime().equals(first.startTime())) {
            throw new IllegalArgumentException("Dataset identity start time does not match its bars");
        }

        if (!identity.endTime().equals(last.endTime())) {
            throw new IllegalArgumentException("Dataset identity end time does not match its bars");
        }

        if (identity.barCount() != bars.size()) {
            throw new IllegalArgumentException("Dataset identity bar count does not match its bars");
        }

        String expectedHash = DatasetFingerprint.sha256(first.instrument(), first.timeframe(), bars);

        if (!identity.contentHash().equals(expectedHash)) {
            throw new IllegalArgumentException("Dataset identity content hash does not match its bars");
        }
    }
}
