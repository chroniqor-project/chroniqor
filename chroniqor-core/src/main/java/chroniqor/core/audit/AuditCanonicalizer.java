/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.audit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

/**
 * Produces the stable binary representation used by the audit fingerprint.
 *
 * <p>This implementation detail is intentionally package-private; callers
 * consume the resulting fingerprint through {@link AuditTrail}.
 */
final class AuditCanonicalizer {
    private static final String SCHEMA = "chroniqor-audit-v1";

    private AuditCanonicalizer() {}

    /**
     * Serializes the trail using a versioned schema and sorted attributes.
     *
     * @param trail validated audit trail
     * @return canonical binary representation
     */
    static byte[] canonicalizer(AuditTrail trail) {

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            DataOutputStream output = new DataOutputStream(bytes);

            writeString(output, SCHEMA);

            output.writeInt(trail.events().size());

            for (AuditEvent event : trail.events()) {

                output.writeLong(event.sequence());

                output.writeLong(event.marketTime().getEpochSecond());

                output.writeInt(event.marketTime().getNano());

                writeString(output, event.type().name());

                TreeMap<String, String> attributes = new TreeMap<>(event.attributes());

                output.writeInt(attributes.size());

                for (Map.Entry<String, String> entry : attributes.entrySet()) {

                    writeString(output, entry.getKey());

                    writeString(output, entry.getValue());
                }
            }

            output.flush();

            return bytes.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Writes a UTF-8 string preceded by its byte length. */
    private static void writeString(DataOutputStream output, String value) throws IOException {

        byte[] encoded = value.getBytes(StandardCharsets.UTF_8);

        output.writeInt(encoded.length);

        output.write(encoded);
    }
}
