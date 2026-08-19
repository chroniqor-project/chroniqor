/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.audit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

final class AuditFingerprint {

    private AuditFingerprint() {}

    static String sha256(AuditTrail trail) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] canonical = AuditCanonicalizer.canonicalizer(trail);

            byte[] hash = digest.digest(canonical);

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
