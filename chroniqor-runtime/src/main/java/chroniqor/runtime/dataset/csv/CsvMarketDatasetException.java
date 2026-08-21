/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.runtime.dataset.csv;

/** Base exception for CSV import failures outside a particular data record. */
public class CsvMarketDatasetException extends RuntimeException {

    public CsvMarketDatasetException(String message) {
        super(message);
    }

    public CsvMarketDatasetException(String message, Throwable cause) {
        super(message, cause);
    }
}
