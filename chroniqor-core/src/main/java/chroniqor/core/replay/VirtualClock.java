/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.replay;

import java.time.Instant;
import java.util.Objects;

/**
 * Mutable clock driven exclusively by caller-supplied market timestamps.
 *
 * <p>The clock invariant is monotonicity: advancing to an earlier instant is
 * rejected.
 */
public final class VirtualClock {

    private Instant currentTime;

    /**
     * Creates a clock at the supplied market time.
     *
     * @param initialTime initial explicit market time
     * @throws NullPointerException if {@code initialTime} is null
     */
    public VirtualClock(Instant initialTime) {
        this.currentTime = Objects.requireNonNull(initialTime, "Initial virtual time must not be null");
    }

    /**
     * Returns the current virtual market time.
     *
     * @return current market time
     */
    public Instant now() {
        return currentTime;
    }

    /**
     * Advances the clock to a target market time.
     *
     * @param targetTime target explicit market time
     * @throws IllegalArgumentException if the target precedes the current
     *     time
     * @throws NullPointerException if {@code targetTime} is null
     */
    public void advanceTo(Instant targetTime) {
        Objects.requireNonNull(targetTime, "Target virtual time must not be null");

        if (targetTime.isBefore(currentTime)) {
            throw new IllegalArgumentException("Virtual clock cannot move backwards");
        }

        currentTime = targetTime;
    }
}
