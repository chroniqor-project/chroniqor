/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.audit;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Immutable event in the ordered audit stream of a replay.
 *
 * <p>Attributes are defensively copied and held in canonical key order. The
 * event's {@code marketTime} is the domain time at which the event occurred;
 * it is not the host clock.
 *
 * @param sequence one-based position of the event in its audit trail
 * @param marketTime market time associated with the event
 * @param type semantic event type
 * @param attributes non-null event attributes with non-null keys and values
 */
public record AuditEvent(long sequence, Instant marketTime, AuditEventType type, Map<String, String> attributes) {

    /**
     * Validates event identity, domain time and canonical attributes.
     *
     * @param sequence one-based event sequence
     * @param marketTime explicit market time
     * @param type event category
     * @param attributes event attributes
     * @throws IllegalArgumentException if {@code sequence} is not positive
     * @throws NullPointerException if any argument, attribute key, or attribute
     *     value is null
     */
    public AuditEvent {
        if (sequence <= 0) {
            throw new IllegalArgumentException("Audit event sequence must be greater than zero");
        }

        Objects.requireNonNull(marketTime, "Audit event market time must not be null");

        Objects.requireNonNull(type, "Audit event type must not be null");

        Objects.requireNonNull(attributes, "Audit event attributes must not be null");

        TreeMap<String, String> canonicalAttributes = new TreeMap<>();

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String key = Objects.requireNonNull(entry.getKey(), "Audit attribute key must not be null");

            String value = Objects.requireNonNull(entry.getValue(), "Audit attribute value must not be null");

            canonicalAttributes.put(key, value);
        }

        attributes = Map.copyOf(canonicalAttributes);
    }
}
