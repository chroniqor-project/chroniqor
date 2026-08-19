/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.replay;

import static org.junit.jupiter.api.Assertions.assertEquals;

import chroniqor.core.dataset.MarketDataset;
import chroniqor.core.market.MarketBar;
import chroniqor.core.testing.SyntheticMarketData;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Virtual clock market time")
class VirtualClockMarketTimeTest {

    @Test
    @DisplayName("advances from market availability times rather than elapsed wall time")
    void shouldAdvanceFromMarketBars() {
        List<MarketBar> bars = SyntheticMarketData.bars().subList(0, 3);
        MarketDataset dataset = MarketDataset.of("synthetic-eurusd", "1", bars);
        VirtualClock clock = new VirtualClock(bars.getFirst().startTime());

        dataset.bars().forEach(bar -> clock.advanceTo(bar.availableAt()));

        assertEquals(bars.getLast().availableAt(), clock.now());
    }
}
