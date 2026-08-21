/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.runtime.dataset.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import chroniqor.core.instrument.CurrencyCode;
import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class CsvMarketDatasetValidationTest {

    private static final CsvDatasetMetadata METADATA = new CsvDatasetMetadata(
            "eurusd-synthetic", "1", new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD")), Timeframe.M5);

    @Test
    void rejectsUnexpectedHeader() {
        String csv = "time,bid_open,bid_high,bid_low,bid_close,ask_open,ask_high,ask_low,ask_close,tick_volume\n";

        CsvMarketDatasetException exception =
                assertThrows(CsvMarketDatasetException.class, () -> new CsvMarketDatasetReader()
                        .read(new StringReader(csv), METADATA));

        assertTrue(exception.getMessage().contains("Invalid CSV header"));
    }

    @Test
    void reportsRecordNumberForWrongColumnCount() {
        String csv = header() + "2026-01-01T10:00:00Z,1.1000\n";

        CsvMarketDatasetRowException exception =
                assertThrows(CsvMarketDatasetRowException.class, () -> new CsvMarketDatasetReader()
                        .read(new StringReader(csv), METADATA));

        assertEquals(1, exception.recordNumber());
        assertTrue(exception.getMessage().contains("expected 10 columns"));
    }

    @Test
    void reportsInvalidTimestampWithRecordContext() {
        String csv = header() + "2026-01-01 10:00:00,1.1000,1.1010,1.0990,1.1005,1.1002,1.1012,1.0992,1.1007,125\n";

        CsvMarketDatasetRowException exception =
                assertThrows(CsvMarketDatasetRowException.class, () -> new CsvMarketDatasetReader()
                        .read(new StringReader(csv), METADATA));

        assertEquals(1, exception.recordNumber());
        assertTrue(exception.getMessage().contains("invalid timestamp"));
    }

    @Test
    void reportsInvalidPriceWithRecordContext() {
        String csv =
                header() + "2026-01-01T10:00:00Z,not-a-price,1.1010,1.0990,1.1005,1.1002,1.1012,1.0992,1.1007,125\n";

        CsvMarketDatasetRowException exception =
                assertThrows(CsvMarketDatasetRowException.class, () -> new CsvMarketDatasetReader()
                        .read(new StringReader(csv), METADATA));

        assertEquals(1, exception.recordNumber());
        assertTrue(exception.getMessage().contains("invalid price in column bid_open"));
    }

    @Test
    void reportsInvalidTickVolumeWithRecordContext() {
        String csv = header()
                + "2026-01-01T10:00:00Z,1.1000,1.1010,1.0990,1.1005,1.1002,1.1012,1.0992,1.1007,not-a-volume\n";

        CsvMarketDatasetRowException exception =
                assertThrows(CsvMarketDatasetRowException.class, () -> new CsvMarketDatasetReader()
                        .read(new StringReader(csv), METADATA));

        assertEquals(1, exception.recordNumber());
        assertTrue(exception.getMessage().contains("invalid integer in column tick_volume"));
    }

    @Test
    void delegatesFinancialValidationToCore() {
        String csv = header() + "2026-01-01T10:00:00Z,1.1000,1.1010,1.0990,1.1005,1.0999,1.1012,1.0992,1.1007,125\n";

        CsvMarketDatasetRowException exception =
                assertThrows(CsvMarketDatasetRowException.class, () -> new CsvMarketDatasetReader()
                        .read(new StringReader(csv), METADATA));

        assertTrue(exception.getMessage().contains("Ask open price must not be lower than bid open price"));
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void preservesGapsButRejectsDuplicateTimestampsThroughCore() {
        String csv = header()
                + "2026-01-01T10:00:00Z,1.1000,1.1010,1.0990,1.1005,1.1002,1.1012,1.0992,1.1007,125\n"
                + "2026-01-01T10:10:00Z,1.1005,1.1020,1.1000,1.1015,1.1007,1.1022,1.1002,1.1017,130\n";

        assertEquals(
                2,
                new CsvMarketDatasetReader()
                        .read(new StringReader(csv), METADATA)
                        .size());

        String duplicate = header()
                + "2026-01-01T10:00:00Z,1.1000,1.1010,1.0990,1.1005,1.1002,1.1012,1.0992,1.1007,125\n"
                + "2026-01-01T10:00:00Z,1.1005,1.1020,1.1000,1.1015,1.1007,1.1022,1.1002,1.1017,130\n";

        CsvMarketDatasetException exception =
                assertThrows(CsvMarketDatasetException.class, () -> new CsvMarketDatasetReader()
                        .read(new StringReader(duplicate), METADATA));

        assertTrue(exception.getMessage().contains("core dataset invariants"));
        assertTrue(exception.getCause().getMessage().contains("duplicated bar timestamps"));
    }

    private static String header() {
        return "timestamp,bid_open,bid_high,bid_low,bid_close,ask_open,ask_high,ask_low,ask_close,tick_volume\n";
    }
}
