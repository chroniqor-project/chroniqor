/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Strategy metadata")
class StrategyMetadataTest {

    @Test
    @DisplayName("stores an explicit id and version")
    void shouldStoreIdAndVersion() {
        StrategyMetadata metadata = new StrategyMetadata("no-op", "1.0.0");

        assertEquals("no-op", metadata.id());
        assertEquals("1.0.0", metadata.version());
    }

    @Test
    @DisplayName("rejects null and blank metadata fields")
    void shouldRejectInvalidMetadata() {
        assertThrows(NullPointerException.class, () -> new StrategyMetadata(null, "1.0.0"));
        assertThrows(NullPointerException.class, () -> new StrategyMetadata("no-op", null));
        assertThrows(IllegalArgumentException.class, () -> new StrategyMetadata("", "1.0.0"));
        assertThrows(IllegalArgumentException.class, () -> new StrategyMetadata("no-op", ""));
        assertThrows(IllegalArgumentException.class, () -> new StrategyMetadata(" no-op", "1.0.0"));
        assertThrows(IllegalArgumentException.class, () -> new StrategyMetadata("no-op", "1.0.0 "));
    }
}
