/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.dataset;

import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import java.time.Instant;
import java.util.Objects;

/**
 * Immutable identity and integrity metadata for a market dataset.
 *
 * @param datasetId stable logical dataset identifier
 * @param version caller-defined dataset version
 * @param instrument instrument contained by the dataset
 * @param timeframe bar timeframe
 * @param startTime start of the first bar
 * @param endTime end of the last bar
 * @param barCount number of bars in the dataset
 * @param contentHash lowercase SHA-256 hash of the canonical bar content
 */
public record DatasetIdentity(
        String datasetId,
        String version,
        CurrencyPair instrument,
        Timeframe timeframe,
        Instant startTime,
        Instant endTime,
        int barCount,
        String contentHash) {

    /**
     * Validates the identity fields and content hash.
     *
     * @param datasetId stable dataset identifier
     * @param version dataset version
     * @param instrument dataset instrument
     * @param timeframe bar timeframe
     * @param startTime first bar start time
     * @param endTime last bar end time
     * @param barCount number of bars
     * @param contentHash canonical content hash
     * @throws IllegalArgumentException if a text field is blank or padded, the
     *     time range is invalid, the bar count is not positive, or the hash is
     *     not a lowercase SHA-256 value
     * @throws NullPointerException if a required value is null
     */
    public DatasetIdentity {
        datasetId = requireNonBlank(datasetId, "Dataset id");
        version = requireNonBlank(version, "Dataset version");

        Objects.requireNonNull(instrument, "Dataset instrument must not be null");

        Objects.requireNonNull(timeframe, "Dataset timeframe must not be null");

        Objects.requireNonNull(startTime, "Dataset start time must not be null");

        Objects.requireNonNull(endTime, "Dataset end time must not be null");

        Objects.requireNonNull(contentHash, "Dataset content hash must not be null");

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Dataset start time must be before end time");
        }

        if (barCount <= 0) {
            throw new IllegalArgumentException("Dataset bar count must be greater than zero");
        }

        if (!contentHash.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("Dataset content hash must be a lowercase SHA-256 hexadecimal value");
        }
    }

    private static String requireNonBlank(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }

        if (!value.equals(value.trim())) {
            throw new IllegalArgumentException(fieldName + " must not contain surrounding whitespace");
        }

        return value;
    }
}
