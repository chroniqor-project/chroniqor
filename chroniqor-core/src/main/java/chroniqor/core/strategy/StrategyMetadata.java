/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

import java.util.Objects;

public record StrategyMetadata(String id, String version) {
    public StrategyMetadata {
        id = requireNonBlank(id, "Strategy id");
        version = requireNonBlank(version, "Strategy version");
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
