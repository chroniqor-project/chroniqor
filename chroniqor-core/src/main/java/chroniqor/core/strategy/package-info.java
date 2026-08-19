/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * Contracts and immutable context objects used to evaluate strategies over
 * the market history visible at a replay step.
 *
 * <p>The context exposes only bars already available at its
 * {@code marketTime}; future dataset bars are intentionally inaccessible.
 */
@org.jspecify.annotations.NullMarked
package chroniqor.core.strategy;
