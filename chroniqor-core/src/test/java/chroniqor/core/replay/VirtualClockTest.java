/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.replay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Virtual clock")
class VirtualClockTest {

    @Test
    @DisplayName("starts at the exact provided instant")
    void shouldStartAtProvidedTime() {
        Instant initialTime = Instant.parse("2026-01-01T10:00:00Z");

        VirtualClock clock = new VirtualClock(initialTime);

        assertEquals(initialTime, clock.now());
    }

    @Test
    @DisplayName("rejects a null initial time")
    void shouldRejectNullInitialTime() {
        assertThrows(NullPointerException.class, () -> new VirtualClock(null));
    }

    @Test
    @DisplayName("advances to a later instant")
    void shouldAdvanceToLaterTime() {
        VirtualClock clock = new VirtualClock(Instant.parse("2026-01-01T10:00:00Z"));
        Instant target = Instant.parse("2026-01-01T10:05:00Z");

        clock.advanceTo(target);

        assertEquals(target, clock.now());
    }

    @Test
    @DisplayName("allows advancing to the same instant")
    void shouldAllowAdvancingToSameTime() {
        Instant time = Instant.parse("2026-01-01T10:00:00Z");
        VirtualClock clock = new VirtualClock(time);

        clock.advanceTo(time);

        assertEquals(time, clock.now());
    }

    @Test
    @DisplayName("rejects moving backwards")
    void shouldRejectMovingBackwards() {
        VirtualClock clock = new VirtualClock(Instant.parse("2026-01-01T10:05:00Z"));

        assertThrows(IllegalArgumentException.class, () -> clock.advanceTo(Instant.parse("2026-01-01T10:04:00Z")));
    }

    @Test
    @DisplayName("keeps its state after a rejected backwards movement")
    void shouldKeepCurrentTimeAfterRejectedBackwardMovement() {
        Instant initialTime = Instant.parse("2026-01-01T10:05:00Z");
        VirtualClock clock = new VirtualClock(initialTime);

        assertThrows(IllegalArgumentException.class, () -> clock.advanceTo(Instant.parse("2026-01-01T10:04:00Z")));

        assertEquals(initialTime, clock.now());
    }

    @Test
    @DisplayName("rejects a null target time")
    void shouldRejectNullTargetTime() {
        VirtualClock clock = new VirtualClock(Instant.parse("2026-01-01T10:00:00Z"));

        assertThrows(NullPointerException.class, () -> clock.advanceTo(null));
    }

    @Test
    @DisplayName("allows temporal gaps")
    void shouldAllowTemporalGaps() {
        VirtualClock clock = new VirtualClock(Instant.parse("2026-01-01T10:01:00Z"));

        clock.advanceTo(Instant.parse("2026-01-01T10:06:00Z"));

        assertEquals(Instant.parse("2026-01-01T10:06:00Z"), clock.now());
    }

    @Test
    @DisplayName("preserves nanosecond precision")
    void shouldPreserveNanosecondPrecision() {
        Instant initial = Instant.parse("2026-01-01T10:00:00.000000001Z");
        Instant target = Instant.parse("2026-01-01T10:00:00.999999999Z");
        VirtualClock clock = new VirtualClock(initial);

        clock.advanceTo(target);

        assertEquals(999_999_999, clock.now().getNano());
        assertTrue(clock.now().isAfter(initial));
    }

    @Test
    @DisplayName("crosses minute and day boundaries without special handling")
    void shouldCrossMinuteAndDayBoundaries() {
        VirtualClock clock = new VirtualClock(Instant.parse("2026-01-01T23:59:59.999999999Z"));
        Instant target = Instant.parse("2026-01-02T00:00:00Z");

        clock.advanceTo(target);

        assertEquals(target, clock.now());
    }

    @Test
    @DisplayName("produces the same final state for the same input sequence")
    void shouldProduceSameTimeForSameSequence() {
        Instant initial = Instant.parse("2026-01-01T10:00:00Z");
        List<Instant> sequence = List.of(
                Instant.parse("2026-01-01T10:01:00Z"),
                Instant.parse("2026-01-01T10:02:00Z"),
                Instant.parse("2026-01-01T10:05:00Z"));

        VirtualClock first = new VirtualClock(initial);
        VirtualClock second = new VirtualClock(initial);

        sequence.forEach(first::advanceTo);
        sequence.forEach(second::advanceTo);

        assertEquals(first.now(), second.now());
    }

    @Test
    @DisplayName("keeps mutable state private and the class final")
    void shouldEncapsulateMutableState() throws NoSuchFieldException {
        Field currentTime = VirtualClock.class.getDeclaredField("currentTime");

        assertTrue(Modifier.isFinal(VirtualClock.class.getModifiers()));
        assertTrue(Modifier.isPrivate(currentTime.getModifiers()));
        assertTrue(!Modifier.isFinal(currentTime.getModifiers()));
    }
}
