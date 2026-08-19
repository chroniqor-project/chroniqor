/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * Executes a strategy over a fixed dataset using an explicit virtual clock.
 *
 * <p>Replay advances only to market availability times, never to wall-clock
 * time, and returns the steps and audit trail needed to reproduce the run.
 */
@org.jspecify.annotations.NullMarked
package chroniqor.core.replay;
