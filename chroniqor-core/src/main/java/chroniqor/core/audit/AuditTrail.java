/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.audit;

import java.util.List;
import java.util.Objects;

public record AuditTrail(List<AuditEvent> events) {

    public AuditTrail {
        Objects.requireNonNull(events, "Audit events must not be null");

        events = List.copyOf(events);

        if (events.isEmpty()) {
            throw new IllegalArgumentException("Audit trail must contain at least one event");
        }

        validateSequence(events);
        validateMarketTime(events);
    }

    public int size() {
        return events.size();
    }

    public String fingerprint() {
        return AuditFingerprint.sha256(this);
    }

    private static void validateSequence(List<AuditEvent> events) {
        if (events.getFirst().sequence() != 1L) {
            throw new IllegalArgumentException("Audit event sequences must be contiguous and start at one");
        }

        for (int index = 1; index < events.size(); index++) {
            long expectedSequence = index + 1L;

            long actualSequence = events.get(index).sequence();

            if (actualSequence != expectedSequence) {
                throw new IllegalArgumentException("Audit event sequences must be contiguous and start at one");
            }
        }
    }

    private static void validateMarketTime(List<AuditEvent> events) {
        for (int index = 1; index < events.size(); index++) {

            AuditEvent previous = events.get(index - 1);

            AuditEvent current = events.get(index);

            if (current.marketTime().isBefore(previous.marketTime())) {
                throw new IllegalArgumentException("Audit event market time must not move backwards");
            }
        }
    }
}
