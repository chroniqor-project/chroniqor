/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.runtime.dataset.csv;

import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import java.util.Objects;

/** Metadata supplied by the caller for one imported CSV dataset. */
public record CsvDatasetMetadata(String datasetId, String version, CurrencyPair instrument, Timeframe timeframe) {

    public CsvDatasetMetadata {
        datasetId = requireNonBlank(datasetId, "Dataset id");
        version = requireNonBlank(version, "Dataset version");

        Objects.requireNonNull(instrument, "Dataset instrument must not be null");
        Objects.requireNonNull(timeframe, "Dataset timeframe must not be null");
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
