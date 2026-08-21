/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.runtime.dataset.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import chroniqor.core.instrument.CurrencyCode;
import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import chroniqor.core.replay.ReplayEngine;
import chroniqor.core.replay.ReplayResult;
import chroniqor.core.strategy.NoOpStrategy;
import chroniqor.core.strategy.StrategyMetadata;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class CsvReplayPipelineTest {

    private static final CsvDatasetMetadata METADATA = new CsvDatasetMetadata(
            "eurusd-synthetic", "1", new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD")), Timeframe.M5);

    private static final String CSV = """
            timestamp,bid_open,bid_high,bid_low,bid_close,ask_open,ask_high,ask_low,ask_close,tick_volume
            2026-01-01T10:00:00Z,1.1000,1.1010,1.0990,1.1005,1.1002,1.1012,1.0992,1.1007,125
            2026-01-01T10:10:00Z,1.1005,1.1020,1.1000,1.1015,1.1007,1.1022,1.1002,1.1017,130
            """;

    @Test
    void runsCsvDatasetThroughDeterministicReplayAndAudit() {
        CsvMarketDatasetReader reader = new CsvMarketDatasetReader();
        ReplayEngine replayEngine = new ReplayEngine();
        NoOpStrategy strategy = new NoOpStrategy(new StrategyMetadata("noop", "1"));

        ReplayResult first = replayEngine.run(reader.read(new StringReader(CSV), METADATA), strategy);
        ReplayResult second = replayEngine.run(reader.read(new StringReader(CSV), METADATA), strategy);

        assertEquals(2, first.processedBars());
        assertEquals(first.dataset().contentHash(), second.dataset().contentHash());
        assertEquals(first.auditTrail().fingerprint(), second.auditTrail().fingerprint());
        assertEquals(first.auditTrail().size(), second.auditTrail().size());
    }
}
