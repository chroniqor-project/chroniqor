/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * Immutable bid/ask market values, quotes and OHLC bars consumed by
 * strategies and replay.
 *
 * <p>Market timestamps are data supplied by the caller. No type in this
 * package reads system time.
 */
@org.jspecify.annotations.NullMarked
package chroniqor.core.market;
