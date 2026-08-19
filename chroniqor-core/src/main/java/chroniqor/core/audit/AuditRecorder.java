/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.audit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class AuditRecorder {

    private final List<AuditEvent> events = new ArrayList<>();

    public AuditEvent record(AuditEventType type, Instant marketTime, Map<String, String> attributes) {

        Objects.requireNonNull(type, "Audit event type must not be null");

        Objects.requireNonNull(marketTime, "Audit event market time must not be null");

        Objects.requireNonNull(attributes, "Audit event attributes must not be null");

        validateTime(marketTime);

        AuditEvent event = new AuditEvent(events.size() + 1L, marketTime, type, attributes);

        events.add(event);

        return event;
    }

    public AuditTrail snapshot() {
        if (events.isEmpty()) {
            throw new IllegalStateException("Cannot create an audit trail without events");
        }

        return new AuditTrail(events);
    }

    private void validateTime(Instant marketTime) {

        if (events.isEmpty()) {
            return;
        }

        Instant previousTime = events.getLast().marketTime();

        if (marketTime.isBefore(previousTime)) {
            throw new IllegalArgumentException("Audit event market time must not move backwards");
        }
    }
}
