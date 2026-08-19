/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * Defines immutable, chronologically ordered market datasets and their
 * content-based identities.
 *
 * <p>A dataset contains bars for one instrument and timeframe only. Its
 * identity includes the SHA-256 content fingerprint used to detect changes
 * before replay.
 */
@org.jspecify.annotations.NullMarked
package chroniqor.core.dataset;
