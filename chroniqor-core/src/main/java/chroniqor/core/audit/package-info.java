/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * Records the ordered, reproducible evidence produced by a core execution.
 *
 * <p>Audit events use the supplied {@code marketTime}; the package does not
 * consult wall-clock time. Event order, timestamps and canonical attributes
 * therefore remain stable for the same execution inputs.
 */
@org.jspecify.annotations.NullMarked
package chroniqor.core.audit;
