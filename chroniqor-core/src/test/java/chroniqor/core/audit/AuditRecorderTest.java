/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Audit recorder")
class AuditRecorderTest {

    private static final Instant FIRST_TIME = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant SECOND_TIME = Instant.parse("2026-01-01T10:01:00Z");

    @Test
    @DisplayName("assigns strictly consecutive sequences starting at one")
    void shouldAssignConsecutiveSequences() {
        AuditRecorder recorder = new AuditRecorder();

        AuditEvent first = recorder.record(AuditEventType.REPLAY_STARTED, FIRST_TIME, Map.of());
        AuditEvent second = recorder.record(AuditEventType.REPLAY_COMPLETED, SECOND_TIME, Map.of());

        assertEquals(1L, first.sequence());
        assertEquals(2L, second.sequence());
    }

    @Test
    @DisplayName("allows equal timestamps but rejects backwards time")
    void shouldEnforceNonDecreasingTime() {
        AuditRecorder recorder = new AuditRecorder();
        recorder.record(AuditEventType.REPLAY_STARTED, FIRST_TIME, Map.of());
        recorder.record(AuditEventType.MARKET_BAR_AVAILABLE, FIRST_TIME, Map.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> recorder.record(AuditEventType.REPLAY_COMPLETED, FIRST_TIME.minusNanos(1), Map.of()));
    }

    @Test
    @DisplayName("rejects null record arguments")
    void shouldRejectNullArguments() {
        AuditRecorder recorder = new AuditRecorder();

        assertThrows(NullPointerException.class, () -> recorder.record(null, FIRST_TIME, Map.of()));
        assertThrows(NullPointerException.class, () -> recorder.record(AuditEventType.REPLAY_STARTED, null, Map.of()));
        assertThrows(
                NullPointerException.class, () -> recorder.record(AuditEventType.REPLAY_STARTED, FIRST_TIME, null));
    }

    @Test
    @DisplayName("creates an independent immutable snapshot")
    void shouldCreateIndependentSnapshot() {
        AuditRecorder recorder = new AuditRecorder();
        Map<String, String> attributes = new HashMap<>();
        attributes.put("value", "before");

        recorder.record(AuditEventType.REPLAY_STARTED, FIRST_TIME, attributes);
        AuditTrail snapshot = recorder.snapshot();
        attributes.put("value", "after");
        recorder.record(AuditEventType.REPLAY_COMPLETED, SECOND_TIME, Map.of());

        assertEquals(1, snapshot.size());
        assertEquals("before", snapshot.events().getFirst().attributes().get("value"));
        assertThrows(
                UnsupportedOperationException.class, () -> snapshot.events().clear());
    }

    @Test
    @DisplayName("rejects an empty snapshot")
    void shouldRejectEmptySnapshot() {
        assertThrows(IllegalStateException.class, () -> new AuditRecorder().snapshot());
    }
}
