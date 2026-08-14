/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.replay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import chroniqor.core.dataset.MarketDataset;
import chroniqor.core.instrument.CurrencyCode;
import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import chroniqor.core.market.MarketBar;
import chroniqor.core.market.Ohlc;
import chroniqor.core.market.Price;
import chroniqor.core.strategy.NoAction;
import chroniqor.core.strategy.NoOpStrategy;
import chroniqor.core.strategy.StrategyMetadata;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Replay engine")
class ReplayEngineTest {

    private static final CurrencyPair EUR_USD = new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD"));

    private static final StrategyMetadata STRATEGY = new StrategyMetadata("noop", "1.0.0");

    @Test
    @DisplayName("processes every dataset bar exactly once")
    void shouldProcessEveryBarExactlyOnce() {
        MarketDataset dataset = dataset(
                bar("2026-01-01T10:00:00Z", "1.1000"),
                bar("2026-01-01T10:01:00Z", "1.1001"),
                bar("2026-01-01T10:02:00Z", "1.1002"));

        ReplayResult result = new ReplayEngine().run(dataset, new NoOpStrategy(STRATEGY));

        assertEquals(3, result.processedBars());
        assertEquals(3, result.steps().size());

        assertEquals(0, result.steps().get(0).index());
        assertEquals(1, result.steps().get(1).index());
        assertEquals(2, result.steps().get(2).index());
    }

    @Test
    @DisplayName("uses bar availability time as replay market time")
    void shouldUseBarAvailabilityTime() {
        MarketDataset dataset = dataset(bar("2026-01-01T10:00:00Z", "1.1000"));

        ReplayResult result = new ReplayEngine().run(dataset, new NoOpStrategy(STRATEGY));

        ReplayStep step = result.steps().getFirst();

        assertEquals(Instant.parse("2026-01-01T10:01:00Z"), step.marketTime());

        assertEquals(step.currentBar().availableAt(), step.marketTime());
    }

    @Test
    @DisplayName("records no action for the no-op strategy")
    void shouldRecordNoAction() {
        MarketDataset dataset = dataset(bar("2026-01-01T10:00:00Z", "1.1000"));

        ReplayResult result = new ReplayEngine().run(dataset, new NoOpStrategy(STRATEGY));

        assertSame(NoAction.INSTANCE, result.steps().getFirst().decision());
    }

    @Test
    @DisplayName("rejects a null dataset")
    void shouldRejectNullDataset() {
        ReplayEngine engine = new ReplayEngine();

        NoOpStrategy strategy = new NoOpStrategy(STRATEGY);

        assertThrows(NullPointerException.class, () -> engine.run(null, strategy));
    }

    @Test
    @DisplayName("rejects a null strategy")
    void shouldRejectNullStrategy() {
        MarketDataset dataset = dataset(bar("2026-01-01T10:00:00Z", "1.1000"));

        assertThrows(NullPointerException.class, () -> new ReplayEngine().run(dataset, null));
    }

    @Test
    @DisplayName("exposes an immutable replay step sequence")
    void shouldExposeImmutableSteps() {
        MarketDataset dataset = dataset(bar("2026-01-01T10:00:00Z", "1.1000"));

        ReplayResult result = new ReplayEngine().run(dataset, new NoOpStrategy(STRATEGY));

        assertThrows(UnsupportedOperationException.class, () -> result.steps().clear());
    }

    private static MarketDataset dataset(MarketBar... bars) {

        return MarketDataset.of("synthetic-eurusd", "1", List.of(bars));
    }

    private static MarketBar bar(String startTime, String bidValue) {

        Price bid = Price.of(bidValue);

        Price ask = new Price(bid.value().add(new java.math.BigDecimal("0.0002")));

        return new MarketBar(
                EUR_USD,
                Timeframe.M1,
                Instant.parse(startTime),
                new Ohlc(bid, bid, bid, bid),
                new Ohlc(ask, ask, ask, ask));
    }
}
