/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

import chroniqor.core.dataset.MarketDataset;
import chroniqor.core.market.MarketBar;
import java.util.List;
import java.util.Objects;

public final class MarketHistory {

    private final List<MarketBar> visibleBars;

    private MarketHistory(MarketDataset dataset, int visibleBarCount) {
        Objects.requireNonNull(dataset, "Market dataset must not be null");

        if (visibleBarCount <= 0) {
            throw new IllegalArgumentException("Visible bar count must be greater than zero");
        }

        if (visibleBarCount > dataset.size()) {
            throw new IllegalArgumentException("Visible bar count must not exceed dataset size");
        }

        this.visibleBars = List.copyOf(dataset.bars().subList(0, visibleBarCount));
    }

    public static MarketHistory fromDataset(MarketDataset dataset, int visibleBarCount) {
        return new MarketHistory(dataset, visibleBarCount);
    }

    public int size() {
        return visibleBars.size();
    }

    public MarketBar current() {
        return visibleBars.getLast();
    }

    public MarketBar getFromLatest(int offset) {
        if (offset < 0 || offset >= visibleBars.size()) {
            throw new IndexOutOfBoundsException("History offset is outside the visible market history");
        }

        return visibleBars.get(visibleBars.size() - 1 - offset);
    }

    public List<MarketBar> asList() {
        return visibleBars;
    }

    public List<MarketBar> visibleBars() {
        return visibleBars;
    }
}
