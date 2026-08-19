/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

/**
 * Deterministic strategy contract evaluated against one visible market
 * context at a time.
 */
public interface Strategy {

    /**
     * Returns the stable identity of this strategy.
     *
     * @return non-null strategy metadata
     */
    StrategyMetadata metadata();

    /**
     * Evaluates the current context without reading future data or host time.
     *
     * @param context visible market history and its explicit market time
     * @return non-null decision for the context
     * @throws NullPointerException if {@code context} is null
     */
    StrategyDecision evaluate(StrategyContext context);
}
