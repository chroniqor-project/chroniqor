/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.replay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import chroniqor.core.audit.AuditEventType;
import chroniqor.core.audit.AuditTrail;
import chroniqor.core.dataset.MarketDataset;
import chroniqor.core.instrument.CurrencyCode;
import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import chroniqor.core.market.MarketBar;
import chroniqor.core.market.Ohlc;
import chroniqor.core.market.Price;
import chroniqor.core.strategy.NoOpStrategy;
import chroniqor.core.strategy.StrategyMetadata;
import chroniqor.core.testing.SyntheticMarketData;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Replay audit integration")
class ReplayAuditIntegrationTest {

    private static final CurrencyPair EUR_USD = new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD"));
    private static final StrategyMetadata STRATEGY = new StrategyMetadata("noop", "1.0.0");

    @Test
    @DisplayName("emits two events per bar plus start and completion")
    void shouldEmitExpectedEventSequence() {
        MarketDataset dataset = MarketDataset.of(
                "synthetic-eurusd", "1", SyntheticMarketData.bars().subList(0, 3));

        AuditTrail audit = replay(dataset).auditTrail();

        assertEquals(8, audit.size());
        assertEquals(
                List.of(
                        AuditEventType.REPLAY_STARTED,
                        AuditEventType.MARKET_BAR_AVAILABLE,
                        AuditEventType.STRATEGY_DECISION_RECORDED,
                        AuditEventType.MARKET_BAR_AVAILABLE,
                        AuditEventType.STRATEGY_DECISION_RECORDED,
                        AuditEventType.MARKET_BAR_AVAILABLE,
                        AuditEventType.STRATEGY_DECISION_RECORDED,
                        AuditEventType.REPLAY_COMPLETED),
                audit.events().stream().map(event -> event.type()).toList());
        assertEquals(
                List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L),
                audit.events().stream().map(event -> event.sequence()).toList());
    }

    @Test
    @DisplayName("preserves market times and does not create events inside gaps")
    void shouldPreserveGapsWithoutSyntheticEvents() {
        MarketDataset dataset = dataset(bar("2026-01-01T10:00:00Z", "1.1000"), bar("2026-01-01T10:05:00Z", "1.1005"));

        AuditTrail audit = replay(dataset).auditTrail();

        assertEquals(6, audit.size());
        assertEquals(
                List.of(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        Instant.parse("2026-01-01T10:01:00Z"),
                        Instant.parse("2026-01-01T10:01:00Z"),
                        Instant.parse("2026-01-01T10:06:00Z"),
                        Instant.parse("2026-01-01T10:06:00Z"),
                        Instant.parse("2026-01-01T10:06:00Z")),
                audit.events().stream().map(event -> event.marketTime()).toList());
    }

    @Test
    @DisplayName("reproduces the same audit trail and fingerprint")
    void shouldBeDeterministic() {
        MarketDataset dataset = MarketDataset.of(
                "synthetic-eurusd", "1", SyntheticMarketData.bars().subList(0, 3));

        ReplayResult first = replay(dataset);
        ReplayResult second = replay(dataset);

        assertEquals(first.auditTrail(), second.auditTrail());
        assertEquals(first.auditTrail().fingerprint(), second.auditTrail().fingerprint());
    }

    @Test
    @DisplayName("does not contaminate audit sequence when replay engine is reused")
    void shouldIsolateRecorderBetweenRuns() {
        MarketDataset dataset = MarketDataset.of(
                "synthetic-eurusd", "1", SyntheticMarketData.bars().subList(0, 2));
        ReplayEngine engine = new ReplayEngine();

        AuditTrail first = engine.run(dataset, new NoOpStrategy(STRATEGY)).auditTrail();
        AuditTrail second = engine.run(dataset, new NoOpStrategy(STRATEGY)).auditTrail();

        assertEquals(6, first.size());
        assertEquals(6, second.size());
        assertEquals(1L, first.events().getFirst().sequence());
        assertEquals(1L, second.events().getFirst().sequence());
        assertEquals(first, second);
    }

    @Test
    @DisplayName("keeps audit trail immutable in the replay result")
    void shouldExposeImmutableAuditTrail() {
        MarketDataset dataset = MarketDataset.of(
                "synthetic-eurusd", "1", SyntheticMarketData.bars().subList(0, 1));

        AuditTrail audit = replay(dataset).auditTrail();

        org.junit.jupiter.api.Assertions.assertThrows(
                UnsupportedOperationException.class, () -> audit.events().clear());
        assertSame(audit.events().getFirst().type(), AuditEventType.REPLAY_STARTED);
    }

    private static ReplayResult replay(MarketDataset dataset) {
        return new ReplayEngine().run(dataset, new NoOpStrategy(STRATEGY));
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
}
