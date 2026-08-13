/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import chroniqor.core.dataset.MarketDataset;
import chroniqor.core.testing.SyntheticMarketData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("No-op strategy")
class NoOpStrategyTest {

    private static final StrategyMetadata METADATA = new StrategyMetadata("no-op", "1.0.0");

    @Test
    @DisplayName("returns the configured metadata")
    void shouldExposeMetadata() {
        NoOpStrategy strategy = new NoOpStrategy(METADATA);

        org.junit.jupiter.api.Assertions.assertEquals(METADATA, strategy.metadata());
    }

    @Test
    @DisplayName("always returns the NoAction singleton")
    void shouldAlwaysReturnNoAction() {
        MarketDataset dataset = MarketDataset.of(
                "synthetic-eurusd", "1", SyntheticMarketData.bars().subList(0, 3));
        StrategyContext context = new StrategyContext(MarketHistory.fromDataset(dataset, 3));
        NoOpStrategy strategy = new NoOpStrategy(METADATA);

        assertSame(NoAction.INSTANCE, strategy.evaluate(context));
        assertSame(NoAction.INSTANCE, strategy.evaluate(context));
    }

    @Test
    @DisplayName("rejects null metadata and context")
    void shouldRejectNullInputs() {
        assertThrows(NullPointerException.class, () -> new NoOpStrategy(null));
        NoOpStrategy strategy = new NoOpStrategy(METADATA);
        assertThrows(NullPointerException.class, () -> strategy.evaluate(null));
    }
}
