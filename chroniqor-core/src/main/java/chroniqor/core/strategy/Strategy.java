/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

public interface Strategy {

    StrategyMetadata metadata();

    StrategyDecision evaluate(StrategyContext context);
}
