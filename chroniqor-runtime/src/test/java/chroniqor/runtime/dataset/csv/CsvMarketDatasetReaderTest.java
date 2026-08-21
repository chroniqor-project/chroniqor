/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.runtime.dataset.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import chroniqor.core.dataset.MarketDataset;
import chroniqor.core.instrument.CurrencyCode;
import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class CsvMarketDatasetReaderTest {

    private static final CsvDatasetMetadata METADATA = new CsvDatasetMetadata(
            "eurusd-synthetic", "1", new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD")), Timeframe.M5);

    private static final String CSV = """
            timestamp,bid_open,bid_high,bid_low,bid_close,ask_open,ask_high,ask_low,ask_close,tick_volume
            2026-01-01T10:00:00Z,1.1000,1.1010,1.0990,1.1005,1.1002,1.1012,1.0992,1.1007,125
            2026-01-01T10:10:00Z,1.1005,1.1020,1.1000,1.1015,1.1007,1.1022,1.1002,1.1017,130
            """;

    @Test
    void readsUtf8CsvIntoValidatedDataset() {
        MarketDataset dataset = new CsvMarketDatasetReader().read(new StringReader(CSV), METADATA);

        assertEquals(2, dataset.size());
        assertEquals(METADATA.instrument(), dataset.identity().instrument());
        assertEquals(METADATA.timeframe(), dataset.identity().timeframe());
        assertEquals("1.1", dataset.bars().getFirst().bid().open().toString());
        assertEquals(125, dataset.bars().getFirst().tickVolume());
        assertEquals(
                "2026-01-01T10:10:00Z", dataset.bars().getLast().startTime().toString());
    }

    @Test
    void readsPathAsUtf8AndStripsUtf8Bom() throws IOException {
        Path path = Files.createTempFile("chroniqor-market", ".csv");

        try {
            Files.writeString(path, "\uFEFF" + CSV, StandardCharsets.UTF_8);

            MarketDataset dataset = new CsvMarketDatasetReader().read(path, METADATA);

            assertEquals(2, dataset.size());
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void doesNotCloseCallerOwnedReader() {
        TrackingReader reader = new TrackingReader(CSV);

        new CsvMarketDatasetReader().read(reader, METADATA);

        assertFalse(reader.closed);
    }

    private static final class TrackingReader extends StringReader {

        private boolean closed;

        private TrackingReader(String value) {
            super(value);
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}
