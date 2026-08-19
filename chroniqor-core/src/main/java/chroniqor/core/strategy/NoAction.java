/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

/** A strategy decision that requests no operation. */
public enum NoAction implements StrategyDecision {
    /** The strategy requests no operation. */
    INSTANCE;

    @Override
    /**
     * Returns the stable audit code.
     *
     * @return {@code NOT_ACTION}
     */
    public String code() {
        return "NOT_ACTION";
    }
}
