/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.instrument;

import java.time.Duration;

/** Supported market-bar durations in V0.1. */
public enum Timeframe {
    /** One-minute bars. */
    M1(Duration.ofMinutes(1)),
    /** Five-minute bars. */
    M5(Duration.ofMinutes(5));

    private final Duration duration;

    Timeframe(Duration duration) {
        this.duration = duration;
    }

    /**
     * Returns the duration represented by this timeframe.
     *
     * @return positive bar duration
     */
    public Duration duration() {
        return duration;
    }
}
