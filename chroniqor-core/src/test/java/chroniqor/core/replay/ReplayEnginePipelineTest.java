/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.replay;

import static org.junit.jupiter.api.Assertions.assertEquals;

import chroniqor.core.dataset.MarketDataset;
import chroniqor.core.instrument.CurrencyCode;
import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import chroniqor.core.market.MarketBar;
import chroniqor.core.market.Ohlc;
import chroniqor.core.market.Price;
import chroniqor.core.strategy.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Deterministic replay pipeline")
class ReplayEnginePipelineTest {

    private static final CurrencyPair EUR_USD = new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD"));

    @Test
    @DisplayName("preserves continuous market time")
    void shouldReplayContinuousBars() {
        MarketDataset dataset = dataset(
                bar("2026-01-01T10:00:00Z", "1.1000"),
                bar("2026-01-01T10:01:00Z", "1.1001"),
                bar("2026-01-01T10:02:00Z", "1.1002"));

        ReplayResult result = replay(dataset);

        assertEquals(
                List.of(
                        Instant.parse("2026-01-01T10:01:00Z"),
                        Instant.parse("2026-01-01T10:02:00Z"),
                        Instant.parse("2026-01-01T10:03:00Z")),
                result.steps().stream().map(ReplayStep::marketTime).toList());
    }

    @Test
    @DisplayName("jumps directly across dataset gaps")
    void shouldReplayDatasetGaps() {
        MarketDataset dataset = dataset(
                bar("2026-01-01T10:00:00Z", "1.1000"),
                bar("2026-01-01T10:01:00Z", "1.1001"),
                bar("2026-01-01T10:05:00Z", "1.1005"));

        ReplayResult result = replay(dataset);

        assertEquals(
                List.of(
                        Instant.parse("2026-01-01T10:01:00Z"),
                        Instant.parse("2026-01-01T10:02:00Z"),
                        Instant.parse("2026-01-01T10:06:00Z")),
                result.steps().stream().map(ReplayStep::marketTime).toList());

        assertEquals(Instant.parse("2026-01-01T10:06:00Z"), result.completedAt());
    }

    @Test
    @DisplayName("produces the same replay result for identical inputs")
    void shouldProduceDeterministicResult() {
        MarketDataset dataset = dataset(
                bar("2026-01-01T10:00:00Z", "1.1000"),
                bar("2026-01-01T10:01:00Z", "1.1001"),
                bar("2026-01-01T10:05:00Z", "1.1005"));

        ReplayResult first = replay(dataset);

        ReplayResult second = replay(dataset);

        assertEquals(first, second);
    }

    @Test
    @DisplayName("completes at the availability time of the final bar")
    void shouldCompleteAtFinalMarketTime() {
        MarketDataset dataset = dataset(bar("2026-01-01T10:00:00Z", "1.1000"), bar("2026-01-01T10:10:00Z", "1.1010"));

        ReplayResult result = replay(dataset);

        assertEquals(Instant.parse("2026-01-01T10:11:00Z"), result.completedAt());
    }

    private static ReplayResult replay(MarketDataset dataset) {

        return new ReplayEngine().run(dataset, new NoOpStrategy(new StrategyMetadata("noop", "1.0.0")));
    }

    private static MarketDataset dataset(MarketBar... bars) {

        return MarketDataset.of("synthetic-eurusd", "1", List.of(bars));
    }

    private static MarketBar bar(String startTime, String bidValue) {

        Price bid = Price.of(bidValue);

        Price ask = new Price(bid.value().add(new BigDecimal("0.0002")));

        Ohlc bidOhlc = new Ohlc(bid, bid, bid, bid);

        Ohlc askOhlc = new Ohlc(ask, ask, ask, ask);

        return new MarketBar(EUR_USD, Timeframe.M1, Instant.parse(startTime), bidOhlc, askOhlc);
    }

    @Test
    @DisplayName("reveals market history incrementally without future bars")
    void shouldRevealHistoryIncrementally() {
        MarketDataset dataset = dataset(
                bar("2026-01-01T10:00:00Z", "1.1000"),
                bar("2026-01-01T10:01:00Z", "1.1001"),
                bar("2026-01-01T10:02:00Z", "1.1002"));

        RecordingStrategy strategy = new RecordingStrategy();

        new ReplayEngine().run(dataset, strategy);

        assertEquals(List.of(1, 2, 3), strategy.visibleHistorySizes);
    }

    private static final class RecordingStrategy implements Strategy {

        private final List<Integer> visibleHistorySizes = new ArrayList<>();

        @Override
        public StrategyMetadata metadata() {
            return new StrategyMetadata("recording-test-strategy", "1");
        }

        @Override
        public StrategyDecision evaluate(StrategyContext context) {

            visibleHistorySizes.add(context.history().size());

            return NoAction.INSTANCE;
        }
    }
}
