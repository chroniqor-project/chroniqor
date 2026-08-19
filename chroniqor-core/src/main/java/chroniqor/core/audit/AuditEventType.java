/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.audit;

public enum AuditEventType {
    REPLAY_STARTED,
    MARKET_BAR_AVAILABLE,
    STRATEGY_DECISION_RECORDED,
    REPLAY_COMPLETED
}
