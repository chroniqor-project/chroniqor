/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import chroniqor.core.dataset.MarketDataset;
import chroniqor.core.market.MarketBar;
import chroniqor.core.testing.SyntheticMarketData;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Strategy pipeline")
class StrategyPipelineTest {

    @Test
    @DisplayName("keeps future bars outside the strategy decision pipeline")
    void shouldEvaluateOnlyVisibleHistory() {
        List<MarketBar> allBars = SyntheticMarketData.bars().subList(0, 5);
        MarketDataset dataset = MarketDataset.of("synthetic-eurusd", "1", allBars);
        MarketHistory history = MarketHistory.fromDataset(dataset, 3);
        StrategyContext context = new StrategyContext(history);
        Strategy strategy = new NoOpStrategy(new StrategyMetadata("no-op", "1.0.0"));

        StrategyDecision decision = strategy.evaluate(context);

        assertSame(NoAction.INSTANCE, decision);
        assertEquals(3, context.history().size());
        assertEquals(allBars.get(2), context.currentBar());
        assertEquals(allBars.subList(0, 3), context.history().asList());
    }
}
