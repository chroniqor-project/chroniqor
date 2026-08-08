/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import org.junit.jupiter.api.Test;

public class TimeframeTest {

    @Test
    void shouldExposeSupportedDurations() {
        assertEquals(Duration.ofMinutes(1), Timeframe.M1.duration());
        assertEquals(Duration.ofMinutes(5), Timeframe.M5.duration());
    }
}
