/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

/**
 * Result returned by a strategy for the current visible market history.
 *
 * <p>The V0.1 contract currently exposes only {@link NoAction}; additional
 * decision types must be added to this sealed hierarchy as explicit domain
 * contracts before runtime order handling is introduced.
 */
public sealed interface StrategyDecision permits NoAction {

    /**
     * Returns the stable audit code for this decision.
     *
     * @return non-null decision code
     */
    String code();
}
