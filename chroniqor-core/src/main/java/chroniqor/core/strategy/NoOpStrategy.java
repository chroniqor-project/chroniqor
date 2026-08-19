/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

import java.util.Objects;

/**
 * Reference strategy that always returns {@link NoAction#INSTANCE}.
 */
public final class NoOpStrategy implements Strategy {

    private final StrategyMetadata metadata;

    /**
     * Creates a no-op strategy with stable metadata.
     *
     * @param metadata strategy identity
     * @throws NullPointerException if {@code metadata} is null
     */
    public NoOpStrategy(StrategyMetadata metadata) {
        this.metadata = Objects.requireNonNull(metadata, "Strategy metadata must not be null");
    }

    @Override
    /**
     * Returns the configured metadata.
     *
     * @return strategy metadata
     */
    public StrategyMetadata metadata() {
        return metadata;
    }

    @Override
    /**
     * Ignores the context and returns the no-action singleton.
     *
     * @param context current strategy context
     * @return {@link NoAction#INSTANCE}
     * @throws NullPointerException if {@code context} is null
     */
    public StrategyDecision evaluate(StrategyContext context) {
        Objects.requireNonNull(context, "Strategy context must not be null");

        return NoAction.INSTANCE;
    }
}
