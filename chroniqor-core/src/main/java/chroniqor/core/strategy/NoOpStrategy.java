/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

import java.util.Objects;

public final class NoOpStrategy implements Strategy {

    private final StrategyMetadata metadata;

    public NoOpStrategy(StrategyMetadata metadata) {
        this.metadata = Objects.requireNonNull(metadata, "Strategy metadata must not be null");
    }

    @Override
    public StrategyMetadata metadata() {
        return metadata;
    }

    @Override
    public StrategyDecision evaluate(StrategyContext context) {
        Objects.requireNonNull(context, "Strategy context must not be null");

        return NoAction.INSTANCE;
    }
}
