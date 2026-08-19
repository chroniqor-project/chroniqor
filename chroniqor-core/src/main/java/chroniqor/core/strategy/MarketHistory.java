/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

import chroniqor.core.dataset.MarketDataset;
import chroniqor.core.market.MarketBar;
import java.util.List;
import java.util.Objects;

/**
 * Immutable, bounded prefix of a dataset exposed to a strategy.
 *
 * <p>The latest visible bar is at offset zero. Future bars are never included
 * in this object.
 */
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

    /**
     * Creates a history containing the first {@code visibleBarCount} bars.
     *
     * @param dataset source dataset
     * @param visibleBarCount number of bars visible to the strategy
     * @return immutable visible history
     * @throws IllegalArgumentException if the count is not within one and the
     *     dataset size
     * @throws NullPointerException if {@code dataset} is null
     */
    public static MarketHistory fromDataset(MarketDataset dataset, int visibleBarCount) {
        return new MarketHistory(dataset, visibleBarCount);
    }

    /**
     * Returns the number of visible bars.
     *
     * @return visible bar count
     */
    public int size() {
        return visibleBars.size();
    }

    /**
     * Returns the latest visible bar.
     *
     * @return latest visible bar
     */
    public MarketBar current() {
        return visibleBars.getLast();
    }

    /**
     * Returns a visible bar by offset from the latest bar.
     *
     * @param offset zero for the latest bar, one for the preceding bar, and so
     *     on
     * @return requested visible bar
     * @throws IndexOutOfBoundsException if the offset is negative or outside
     *     the visible history
     */
    public MarketBar getFromLatest(int offset) {
        if (offset < 0 || offset >= visibleBars.size()) {
            throw new IndexOutOfBoundsException("History offset is outside the visible market history");
        }

        return visibleBars.get(visibleBars.size() - 1 - offset);
    }

    /**
     * Returns visible bars in chronological order.
     *
     * @return unmodifiable visible bars
     */
    public List<MarketBar> asList() {
        return visibleBars;
    }

    /**
     * Returns visible bars in chronological order.
     *
     * @return unmodifiable visible bars
     */
    public List<MarketBar> visibleBars() {
        return visibleBars;
    }
}
