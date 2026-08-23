/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.audit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Internal SHA-256 service for canonical audit trails. */
final class AuditFingerprint {

    private AuditFingerprint() {}

    /**
     * Computes the lowercase SHA-256 fingerprint of a canonical trail.
     *
     * @param trail validated audit trail
     * @return 64-character lowercase hexadecimal hash
     * @throws IllegalStateException if the runtime does not provide SHA-256
     */
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
