/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.replay;

import chroniqor.core.audit.AuditEventType;
import chroniqor.core.audit.AuditRecorder;
import chroniqor.core.audit.AuditTrail;
import chroniqor.core.dataset.MarketDataset;
import chroniqor.core.market.MarketBar;
import chroniqor.core.strategy.MarketHistory;
import chroniqor.core.strategy.Strategy;
import chroniqor.core.strategy.StrategyContext;
import chroniqor.core.strategy.StrategyDecision;
import chroniqor.core.strategy.StrategyMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Runs a strategy over an immutable dataset with an explicit virtual clock
 * and deterministic audit trail.
 *
 * <p>At each step the strategy receives only the prefix of bars whose
 * availability time has been reached. The engine never consults host time or
 * infrastructure services.
 */
public final class ReplayEngine {

    /** Creates a replay engine with no external state. */
    public ReplayEngine() {}

    /**
     * Replays {@code strategy} over every bar in {@code dataset}.
     *
     * @param dataset immutable, chronologically ordered market dataset
     * @param strategy strategy to evaluate at each available bar
     * @return validated replay result with steps and audit trail
     * @throws NullPointerException if {@code dataset}, {@code strategy}, its
     *     metadata, or a returned decision is null
     * @throws IllegalStateException if the strategy context time becomes
     *     unsynchronized or generated steps do not match the dataset
     */
    public ReplayResult run(MarketDataset dataset, Strategy strategy) {

        Objects.requireNonNull(dataset, "Market dataset must not be null");

        Objects.requireNonNull(strategy, "Strategy must not be null");

        MarketBar firstBar = dataset.bars().getFirst();

        VirtualClock clock = new VirtualClock(firstBar.startTime());

        List<ReplayStep> steps = new ArrayList<>(dataset.size());

        AuditRecorder audit = new AuditRecorder();

        StrategyMetadata strategyMetadata =
                Objects.requireNonNull(strategy.metadata(), "Strategy metadata must not be null");

        audit.record(
                AuditEventType.REPLAY_STARTED,
                clock.now(),
                Map.of(
                        "datasetId", dataset.identity().datasetId(),
                        "datasetVersion", dataset.identity().version(),
                        "datasetContentHash", dataset.identity().contentHash(),
                        "instrument", dataset.identity().instrument().symbol(),
                        "timeframe", dataset.identity().timeframe().name(),
                        "barCount", Integer.toString(dataset.identity().barCount()),
                        "strategyId", strategyMetadata.id(),
                        "strategyVersion", strategyMetadata.version()));

        for (int index = 0; index < dataset.size(); index++) {
            MarketBar currentBar = dataset.bars().get(index);

            clock.advanceTo(currentBar.availableAt());

            audit.record(
                    AuditEventType.MARKET_BAR_AVAILABLE,
                    clock.now(),
                    Map.of(
                            "barIndex",
                            Integer.toString(index),
                            "barStartTime",
                            currentBar.startTime().toString()));

            MarketHistory history = MarketHistory.fromDataset(dataset, index + 1);

            StrategyContext context = new StrategyContext(history);

            requireSynchronizedTime(clock, context);

            StrategyDecision decision =
                    Objects.requireNonNull(strategy.evaluate(context), "Strategy decision must not be null");

            audit.record(
                    AuditEventType.STRATEGY_DECISION_RECORDED,
                    clock.now(),
                    Map.of("barIndex", Integer.toString(index), "decision", decision.code()));

            steps.add(new ReplayStep(index, clock.now(), currentBar, decision));
        }
        validateReplaySteps(dataset, steps);

        Instant completeAt = clock.now();

        audit.record(
                AuditEventType.REPLAY_COMPLETED,
                completeAt,
                Map.of(
                        "processedBars", Integer.toString(steps.size()),
                        "strategyDecisionCount", Integer.toString(steps.size())));

        AuditTrail auditTrail = audit.snapshot();

        return new ReplayResult(
                dataset.identity(), strategyMetadata, firstBar.startTime(), completeAt, steps, auditTrail);
    }

    private static void validateReplaySteps(MarketDataset dataset, List<ReplayStep> steps) {
        if (steps.size() != dataset.size()) {
            throw new IllegalStateException("Replay step count must match dataset bar count");
        }

        for (int i = 0; i < dataset.size(); i++) {
            MarketBar expectedBar = dataset.bars().get(i);
            MarketBar actualBar = steps.get(i).currentBar();

            if (!actualBar.equals(expectedBar)) {
                throw new IllegalStateException(
                        "Replay step at index " + i + " does not correspond to the expected dataset bar");
            }
        }
    }

    private static void requireSynchronizedTime(VirtualClock clock, StrategyContext context) {

        if (!clock.now().equals(context.marketTime())) {
            throw new IllegalStateException("Virtual clock and strategy market time must remain synchronized");
        }
    }
}
