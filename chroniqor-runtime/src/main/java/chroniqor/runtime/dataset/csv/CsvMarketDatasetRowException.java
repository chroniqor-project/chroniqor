/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.runtime.dataset.csv;

/** CSV import failure associated with one data record. */
public final class CsvMarketDatasetRowException extends CsvMarketDatasetException {

    private final long recordNumber;

    public CsvMarketDatasetRowException(long recordNumber, String message, Throwable cause) {

        super("Invalid CSV market record " + recordNumber + ": " + message, cause);

        if (recordNumber <= 0) {
            throw new IllegalArgumentException("CSV record number must be greater than zero");
        }

        this.recordNumber = recordNumber;
    }

    public long recordNumber() {
        return recordNumber;
    }
}
