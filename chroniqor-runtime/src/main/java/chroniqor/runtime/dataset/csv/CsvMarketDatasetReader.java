/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.runtime.dataset.csv;

import chroniqor.core.dataset.MarketDataset;
import chroniqor.core.market.MarketBar;
import chroniqor.core.market.Ohlc;
import chroniqor.core.market.Price;
import java.io.FilterReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.DuplicateHeaderMode;

/** Reads the fixed V0.1 bid/ask OHLC CSV contract into core domain objects. */
public final class CsvMarketDatasetReader {

    private static final String TIMESTAMP = "timestamp";
    private static final String BID_OPEN = "bid_open";
    private static final String BID_HIGH = "bid_high";
    private static final String BID_LOW = "bid_low";
    private static final String BID_CLOSE = "bid_close";
    private static final String ASK_OPEN = "ask_open";
    private static final String ASK_HIGH = "ask_high";
    private static final String ASK_LOW = "ask_low";
    private static final String ASK_CLOSE = "ask_close";
    private static final String TICK_VOLUME = "tick_volume";

    private static final List<String> REQUIRED_HEADERS = List.of(
            TIMESTAMP, BID_OPEN, BID_HIGH, BID_LOW, BID_CLOSE, ASK_OPEN, ASK_HIGH, ASK_LOW, ASK_CLOSE, TICK_VOLUME);

    private static final CSVFormat FORMAT = CSVFormat.RFC4180
            .builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setAllowMissingColumnNames(false)
            .setDuplicateHeaderMode(DuplicateHeaderMode.DISALLOW)
            .setIgnoreEmptyLines(false)
            .setIgnoreSurroundingSpaces(false)
            .setTrim(false)
            .get();

    /**
     * Reads a UTF-8 CSV file from the filesystem.
     *
     * @param path CSV path
     * @param metadata dataset metadata not stored in each row
     * @return validated immutable market dataset
     */
    public MarketDataset read(Path path, CsvDatasetMetadata metadata) {
        Objects.requireNonNull(path, "CSV path must not be null");

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return read(reader, metadata);
        } catch (IOException exception) {
            throw new CsvMarketDatasetException("Unable to read CSV market dataset from " + path, exception);
        }
    }

    /**
     * Reads a CSV stream without taking ownership of the supplied reader.
     *
     * @param reader CSV character stream
     * @param metadata dataset metadata not stored in each row
     * @return validated immutable market dataset
     */
    public MarketDataset read(Reader reader, CsvDatasetMetadata metadata) {

        Objects.requireNonNull(reader, "CSV reader must not be null");

        Objects.requireNonNull(metadata, "CSV dataset metadata must not be null");

        List<MarketBar> bars = parse(reader, metadata);

        if (bars.isEmpty()) {
            throw new CsvMarketDatasetException("CSV market dataset must contain at least one data record");
        }

        try {
            return MarketDataset.of(metadata.datasetId(), metadata.version(), bars);
        } catch (IllegalArgumentException exception) {
            throw new CsvMarketDatasetException("CSV market dataset violates core dataset invariants", exception);
        }
    }

    private static List<MarketBar> parse(Reader reader, CsvDatasetMetadata metadata) {

        try (CSVParser parser = FORMAT.parse(new NonClosingReader(withoutUtf8Bom(reader)))) {

            validateHeaders(parser.getHeaderNames());

            List<MarketBar> bars = new ArrayList<>();

            for (CSVRecord record : parser) {
                bars.add(parseRecord(record, metadata));
            }

            return List.copyOf(bars);

        } catch (CsvMarketDatasetException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new CsvMarketDatasetException("Unable to parse CSV market dataset", exception);

        } catch (IllegalArgumentException exception) {
            throw new CsvMarketDatasetException("Invalid CSV format", exception);
        }
    }

    private static void validateHeaders(List<String> actualHeaders) {

        if (!actualHeaders.equals(REQUIRED_HEADERS)) {
            throw new CsvMarketDatasetException(
                    "Invalid CSV header. Expected " + REQUIRED_HEADERS + " but found " + actualHeaders);
        }
    }

    private static MarketBar parseRecord(CSVRecord record, CsvDatasetMetadata metadata) {
        try {
            if (record.size() != REQUIRED_HEADERS.size()) {
                throw new IllegalArgumentException(
                        "expected " + REQUIRED_HEADERS.size() + " columns but found " + record.size());
            }

            Instant startTime = parseInstant(record, TIMESTAMP);

            Ohlc bid = new Ohlc(
                    parsePrice(record, BID_OPEN),
                    parsePrice(record, BID_HIGH),
                    parsePrice(record, BID_LOW),
                    parsePrice(record, BID_CLOSE));

            Ohlc ask = new Ohlc(
                    parsePrice(record, ASK_OPEN),
                    parsePrice(record, ASK_HIGH),
                    parsePrice(record, ASK_LOW),
                    parsePrice(record, ASK_CLOSE));

            long tickVolume = parseLong(record, TICK_VOLUME);

            return new MarketBar(metadata.instrument(), metadata.timeframe(), startTime, bid, ask, tickVolume);
        } catch (CsvMarketDatasetRowException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new CsvMarketDatasetRowException(record.getRecordNumber(), messageFor(exception), exception);
        }
    }

    private static Instant parseInstant(CSVRecord record, String column) {
        try {
            return Instant.parse(value(record, column));
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("invalid timestamp in column " + column, exception);
        }
    }

    private static Price parsePrice(CSVRecord record, String column) {
        try {
            return Price.of(value(record, column));
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("invalid price in column " + column, exception);
        }
    }

    private static long parseLong(CSVRecord record, String column) {
        try {
            return Long.parseLong(value(record, column));
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("invalid integer in column " + column, exception);
        }
    }

    private static String value(CSVRecord record, String column) {
        String value = record.get(column);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("column " + column + " must not be blank");
        }

        return value;
    }

    private static String messageFor(RuntimeException exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? "record violates import or core domain invariants" : message;
    }

    private static Reader withoutUtf8Bom(Reader reader) throws IOException {
        PushbackReader pushbackReader = new PushbackReader(reader, 1);
        int firstCharacter = pushbackReader.read();

        if (firstCharacter != -1 && firstCharacter != '\uFEFF') {
            pushbackReader.unread(firstCharacter);
        }

        return pushbackReader;
    }

    private static final class NonClosingReader extends FilterReader {

        private NonClosingReader(Reader reader) {

            super(reader);
        }

        @Override
        public void close() {}
    }
}
