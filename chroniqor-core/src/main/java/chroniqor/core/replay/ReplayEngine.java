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

        MarketBar fitsBar = dataset.bars().getFirst();

        VirtualClock clock = new VirtualClock(fitsBar.startTime());

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
        Instant completeAt = clock.now();

        return new ReplayResult(dataset.identity(), strategy.metadata(), fitsBar.startTime(), completeAt, steps);
    }

    private static void requireSynchronizedTime(VirtualClock clock, StrategyContext context) {

        if (!clock.now().equals(context.marketTime())) {
            throw new IllegalStateException("Virtual clock and strategy market time must remain synchronized");
        }
    }
}
