/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import chroniqor.core.dataset.MarketDataset;
import chroniqor.core.market.MarketBar;
import chroniqor.core.testing.SyntheticMarketData;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Market history")
class MarketHistoryTest {

    @Test
    @DisplayName("exposes only the requested prefix including the current bar")
    void shouldExposeOnlyVisibleBars() {
        List<MarketBar> bars = SyntheticMarketData.bars().subList(0, 5);
        MarketDataset dataset = MarketDataset.of("synthetic-eurusd", "1", bars);

        MarketHistory history = MarketHistory.fromDataset(dataset, 3);

        assertEquals(3, history.size());
        assertEquals(bars.get(0), history.getFromLatest(2));
        assertEquals(bars.get(1), history.getFromLatest(1));
        assertEquals(bars.get(2), history.getFromLatest(0));
        assertEquals(bars.get(2), history.current());
        assertEquals(List.of(bars.get(0), bars.get(1), bars.get(2)), history.asList());
    }

    @Test
    @DisplayName("does not expose future bars through the history view")
    void shouldHideFutureBars() {
        List<MarketBar> bars = SyntheticMarketData.bars().subList(0, 5);
        MarketDataset dataset = MarketDataset.of("synthetic-eurusd", "1", bars);

        MarketHistory history = MarketHistory.fromDataset(dataset, 3);

        assertThrows(IndexOutOfBoundsException.class, () -> history.getFromLatest(3));
        assertEquals(3, history.asList().size());
    }

    @Test
    @DisplayName("rejects invalid visibility sizes and offsets")
    void shouldRejectInvalidSizesAndOffsets() {
        List<MarketBar> bars = SyntheticMarketData.bars().subList(0, 5);
        MarketDataset dataset = MarketDataset.of("synthetic-eurusd", "1", bars);

        assertThrows(IllegalArgumentException.class, () -> MarketHistory.fromDataset(dataset, 0));
        assertThrows(IllegalArgumentException.class, () -> MarketHistory.fromDataset(dataset, 6));
        MarketHistory history = MarketHistory.fromDataset(dataset, 3);
        assertThrows(IndexOutOfBoundsException.class, () -> history.getFromLatest(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> history.getFromLatest(3));
    }

    @Test
    @DisplayName("does not allow callers to modify visible bars")
    void shouldExposeImmutableBars() {
        MarketDataset dataset = MarketDataset.of(
                "synthetic-eurusd", "1", SyntheticMarketData.bars().subList(0, 3));
        MarketHistory history = MarketHistory.fromDataset(dataset, 2);

        assertThrows(UnsupportedOperationException.class, () -> history.asList().clear());
        assertThrows(
                UnsupportedOperationException.class, () -> history.visibleBars().clear());
    }
}
