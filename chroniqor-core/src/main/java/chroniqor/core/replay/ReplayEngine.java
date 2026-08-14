/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.replay;

import chroniqor.core.dataset.MarketDataset;
import chroniqor.core.market.MarketBar;
import chroniqor.core.strategy.MarketHistory;
import chroniqor.core.strategy.Strategy;
import chroniqor.core.strategy.StrategyContext;
import chroniqor.core.strategy.StrategyDecision;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ReplayEngine {

    public ReplayResult run(MarketDataset dataset, Strategy strategy) {

        Objects.requireNonNull(dataset, "Market dataset must not be null");

        Objects.requireNonNull(strategy, "Strategy must not be null");

        MarketBar firstBar = dataset.bars().getFirst();

        VirtualClock clock = new VirtualClock(firstBar.startTime());

        List<ReplayStep> steps = new ArrayList<>(dataset.size());

        for (int index = 0; index < dataset.size(); index++) {
            MarketBar currentBar = dataset.bars().get(index);

            clock.advanceTo(currentBar.availableAt());

            MarketHistory history = MarketHistory.fromDataset(dataset, index + 1);

            StrategyContext context = new StrategyContext(history);

            requireSynchronizedTime(clock, context);

            StrategyDecision decision =
                    Objects.requireNonNull(strategy.evaluate(context), "Strategy decision must not be null");

            steps.add(new ReplayStep(index, clock.now(), currentBar, decision));
        }
        validateReplaySteps(dataset, steps);

        Instant completeAt = clock.now();

        return new ReplayResult(dataset.identity(), strategy.metadata(), firstBar.startTime(), completeAt, steps);
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
