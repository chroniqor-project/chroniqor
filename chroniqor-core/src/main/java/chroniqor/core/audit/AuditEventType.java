/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.audit;

/** The stable categories emitted while a replay is executed. */
public enum AuditEventType {
    /** Marks the beginning of a replay. */
    REPLAY_STARTED,
    /** Records that the current market bar became available. */
    MARKET_BAR_AVAILABLE,
    /** Records the decision returned by the strategy. */
    STRATEGY_DECISION_RECORDED,
    /** Marks the end of a replay. */
    REPLAY_COMPLETED
}
