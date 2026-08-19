/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Audit trail")
class AuditTrailTest {

    @Test
    @DisplayName("requires events to start at sequence one")
    void shouldRequireSequenceOne() {
        assertThrows(IllegalArgumentException.class, () -> new AuditTrail(List.of(event(2, "value"))));
    }

    @Test
    @DisplayName("rejects sequence gaps and non-decreasing time violations")
    void shouldRejectInvalidSequenceAndTime() {
        assertThrows(IllegalArgumentException.class, () -> new AuditTrail(List.of(event(1, "one"), event(3, "three"))));
        assertThrows(
                IllegalArgumentException.class,
                () -> new AuditTrail(List.of(eventAt(1, "2026-01-01T10:01:00Z"), eventAt(2, "2026-01-01T10:00:00Z"))));
    }

    @Test
    @DisplayName("copies the event list and rejects an empty trail")
    void shouldCopyEvents() {
        List<AuditEvent> source = List.of(event(1, "one"));
        AuditTrail trail = new AuditTrail(source);

        assertEquals(source, trail.events());
        assertThrows(UnsupportedOperationException.class, () -> trail.events().clear());
        assertThrows(IllegalArgumentException.class, () -> new AuditTrail(List.of()));
    }

    @Test
    @DisplayName("canonicalizes attribute key order")
    void shouldIgnoreAttributeInsertionOrder() {
        Map<String, String> firstAttributes = new LinkedHashMap<>();
        firstAttributes.put("b", "two");
        firstAttributes.put("a", "one");
        Map<String, String> secondAttributes = new LinkedHashMap<>();
        secondAttributes.put("a", "one");
        secondAttributes.put("b", "two");

        AuditTrail first = new AuditTrail(List.of(new AuditEvent(
                1, Instant.parse("2026-01-01T10:00:00Z"), AuditEventType.REPLAY_STARTED, firstAttributes)));
        AuditTrail second = new AuditTrail(List.of(new AuditEvent(
                1, Instant.parse("2026-01-01T10:00:00Z"), AuditEventType.REPLAY_STARTED, secondAttributes)));

        assertEquals(first.fingerprint(), second.fingerprint());
    }

    @Test
    @DisplayName("canonicalizes all events, not only the first event")
    void shouldIncludeEveryEventInFingerprint() {
        AuditTrail first = new AuditTrail(List.of(event(1, "one")));
        AuditTrail second = new AuditTrail(List.of(event(1, "one"), event(2, "two")));

        assertNotEquals(first.fingerprint(), second.fingerprint());
    }

    @Test
    @DisplayName("uses UTF-8 encoding for attribute values")
    void shouldUseUtf8Encoding() {
        AuditTrail trail = new AuditTrail(List.of(new AuditEvent(
                1, Instant.parse("2026-01-01T10:00:00Z"), AuditEventType.REPLAY_STARTED, Map.of("message", "café"))));

        byte[] canonical = AuditCanonicalizer.canonicalizer(trail);
        String canonicalText = new String(canonical, StandardCharsets.UTF_8);

        assertTrue(canonicalText.contains("café"));
    }

    @Test
    @DisplayName("includes nanosecond precision in the fingerprint")
    void shouldIncludeNanoseconds() {
        AuditTrail first = new AuditTrail(List.of(eventAt(1, "2026-01-01T10:00:00.000000001Z")));
        AuditTrail second = new AuditTrail(List.of(eventAt(1, "2026-01-01T10:00:00.000000002Z")));

        assertNotEquals(first.fingerprint(), second.fingerprint());
    }

    @Test
    @DisplayName("matches the frozen audit V1 golden fingerprint")
    void shouldMatchAuditV1GoldenFingerprint() {
        AuditTrail trail = new AuditTrail(List.of(
                new AuditEvent(
                        1,
                        Instant.parse("2026-01-01T10:00:00Z"),
                        AuditEventType.REPLAY_STARTED,
                        Map.of("dataset", "synthetic", "strategy", "noop")),
                new AuditEvent(
                        2,
                        Instant.parse("2026-01-01T10:01:00.000000001Z"),
                        AuditEventType.MARKET_BAR_AVAILABLE,
                        Map.of("barIndex", "0")),
                new AuditEvent(
                        3,
                        Instant.parse("2026-01-01T10:01:00.000000001Z"),
                        AuditEventType.STRATEGY_DECISION_RECORDED,
                        Map.of("decision", "NO_ACTION")),
                new AuditEvent(
                        4,
                        Instant.parse("2026-01-01T10:01:00.000000001Z"),
                        AuditEventType.REPLAY_COMPLETED,
                        Map.of("processedBars", "1"))));

        assertEquals("08b0161ef004c58a47b394c29d162bd2856432560489502c26890876cb99fbe9", trail.fingerprint());
    }

    private static AuditEvent event(long sequence, String value) {
        return new AuditEvent(
                sequence, Instant.parse("2026-01-01T10:00:00Z"), AuditEventType.REPLAY_STARTED, Map.of("value", value));
    }

    private static AuditEvent eventAt(long sequence, String time) {
        return new AuditEvent(sequence, Instant.parse(time), AuditEventType.REPLAY_STARTED, Map.of());
    }
}
