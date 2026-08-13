/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import chroniqor.core.dataset.MarketDataset;
import chroniqor.core.instrument.Timeframe;
import chroniqor.core.market.MarketBar;
import chroniqor.core.testing.SyntheticMarketData;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Strategy context")
class StrategyContextTest {

    @Test
    @DisplayName("derives state from the current visible bar")
    void shouldDeriveStateFromHistory() {
        List<MarketBar> bars = SyntheticMarketData.bars().subList(0, 5);
        MarketDataset dataset = MarketDataset.of("synthetic-eurusd", "1", bars);
        MarketHistory history = MarketHistory.fromDataset(dataset, 3);

        StrategyContext context = new StrategyContext(history);

        assertEquals(bars.get(2), context.currentBar());
        assertEquals(bars.get(2).availableAt(), context.marketTime());
        assertEquals(bars.get(2).instrument(), context.instrument());
        assertEquals(Timeframe.M1, context.timeframe());
        assertEquals(Instant.parse("2026-01-01T00:03:00Z"), context.marketTime());
    }

    @Test
    @DisplayName("rejects a null history")
    void shouldRejectNullHistory() {
        assertThrows(NullPointerException.class, () -> new StrategyContext(null));
    }
}
