/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.audit;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public record AuditEvent(long sequence, Instant marketTime, AuditEventType type, Map<String, String> attributes) {

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
