/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.instrument;

import java.util.Objects;

public record CurrencyCode(String value) {

    public CurrencyCode {
        Objects.requireNonNull(value, "Currency code must not be null");

        if (!value.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Currency code must contain exactly three uppercase ASCII letters");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
