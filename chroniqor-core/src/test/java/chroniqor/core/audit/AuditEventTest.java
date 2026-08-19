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

@DisplayName("Audit event")
class AuditEventTest {

    private static final Instant TIME = Instant.parse("2026-01-01T10:00:00Z");

    @Test
    @DisplayName("requires a positive sequence")
    void shouldRequirePositiveSequence() {
        assertThrows(IllegalArgumentException.class, () -> event(0, Map.of()));
        assertThrows(IllegalArgumentException.class, () -> event(-1, Map.of()));
    }

    @Test
    @DisplayName("rejects null required values")
    void shouldRejectNullValues() {
        assertThrows(
                NullPointerException.class, () -> new AuditEvent(1, null, AuditEventType.REPLAY_STARTED, Map.of()));
        assertThrows(NullPointerException.class, () -> new AuditEvent(1, TIME, null, Map.of()));
        assertThrows(NullPointerException.class, () -> new AuditEvent(1, TIME, AuditEventType.REPLAY_STARTED, null));
    }

    @Test
    @DisplayName("copies and canonicalizes attributes")
    void shouldCopyAttributes() {
        Map<String, String> source = new HashMap<>();
        source.put("z", "last");
        source.put("a", "first");

        AuditEvent event = event(1, source);
        source.put("new", "value");

        assertEquals(Map.of("a", "first", "z", "last"), event.attributes());
        assertThrows(
                UnsupportedOperationException.class, () -> event.attributes().put("x", "value"));
    }

    @Test
    @DisplayName("rejects null attribute keys and values")
    void shouldRejectNullAttributes() {
        Map<String, String> nullKey = new HashMap<>();
        nullKey.put(null, "value");
        Map<String, String> nullValue = new HashMap<>();
        nullValue.put("key", null);

        assertThrows(NullPointerException.class, () -> event(1, nullKey));
        assertThrows(NullPointerException.class, () -> event(1, nullValue));
    }

    private static AuditEvent event(long sequence, Map<String, String> attributes) {
        return new AuditEvent(sequence, TIME, AuditEventType.REPLAY_STARTED, attributes);
    }
}
