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

/**
 * Mutable builder for a monotonically ordered audit trail.
 *
 * <p>Recorded market times may stay equal but may never move backwards.
 * Calling {@link #snapshot()} creates an immutable trail without exposing the
 * recorder's mutable storage.
 */
public final class AuditRecorder {

    private final List<AuditEvent> events = new ArrayList<>();

    /** Creates an empty audit recorder. */
    public AuditRecorder() {}

    /**
     * Appends an event at the supplied domain time.
     *
     * @param type event category
     * @param marketTime explicit market time for the event
     * @param attributes event attributes
     * @return the newly appended event with its assigned sequence
     * @throws IllegalArgumentException if {@code marketTime} precedes the
     *     previous event's market time
     * @throws NullPointerException if an argument, attribute key, or attribute
     *     value is null
     */
    public AuditEvent record(AuditEventType type, Instant marketTime, Map<String, String> attributes) {

        Objects.requireNonNull(type, "Audit event type must not be null");

        Objects.requireNonNull(marketTime, "Audit event market time must not be null");

        Objects.requireNonNull(attributes, "Audit event attributes must not be null");

        validateTime(marketTime);

        AuditEvent event = new AuditEvent(events.size() + 1L, marketTime, type, attributes);

        events.add(event);

        return event;
    }

    /**
     * Returns an immutable view of all events recorded so far.
     *
     * @return the current non-empty audit trail
     * @throws IllegalStateException if no event has been recorded
     */
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
