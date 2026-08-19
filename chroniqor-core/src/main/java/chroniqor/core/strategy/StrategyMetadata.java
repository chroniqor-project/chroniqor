/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

import java.util.Objects;

/**
 * Stable identity of a strategy implementation and its configuration
 * contract.
 *
 * @param id non-blank strategy identifier without surrounding whitespace
 * @param version non-blank strategy version without surrounding whitespace
 */
public record StrategyMetadata(String id, String version) {
    /**
     * Validates strategy identity fields.
     *
     * @param id strategy identifier
     * @param version strategy version
     * @throws IllegalArgumentException if a value is blank or padded
     * @throws NullPointerException if a value is null
     */
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
