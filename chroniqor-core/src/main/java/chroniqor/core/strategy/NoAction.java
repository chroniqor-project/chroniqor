/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

public enum NoAction implements StrategyDecision {
    INSTANCE;

    @Override
    public String code() {
        return "NOT_ACTION";
    }
}
