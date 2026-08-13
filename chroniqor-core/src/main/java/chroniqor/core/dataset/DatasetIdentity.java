/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.dataset;

import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import java.time.Instant;
import java.util.Objects;

public record DatasetIdentity(
        String datasetId,
        String version,
        CurrencyPair instrument,
        Timeframe timeframe,
        Instant startTime,
        Instant endTime,
        int barCount,
        String contentHash) {

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
            throw new IllegalArgumentException(fieldName + "must not be blank");
        }

        if (!value.equals(value.trim())) {
            throw new IllegalArgumentException(fieldName + " must not contain surrounding whitespace");
        }

        return value;
    }
}
