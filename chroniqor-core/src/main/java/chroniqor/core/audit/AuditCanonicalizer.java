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

final class AuditCanonicalizer {
    private static final String SCHEMA = "chroniqor-audit-v1";

    private AuditCanonicalizer() {}

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

    private static void writeString(DataOutputStream output, String value) throws IOException {

        byte[] encoded = value.getBytes(StandardCharsets.UTF_8);

        output.writeInt(encoded.length);

        output.write(encoded);
    }
}
