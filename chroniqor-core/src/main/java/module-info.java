/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

/** Core domain API for deterministic, auditable market replay. */
module io.github.cvcg11.chroniqor.core {
    requires static org.jspecify;

    exports chroniqor.core.audit;
    exports chroniqor.core.dataset;
    exports chroniqor.core.instrument;
    exports chroniqor.core.market;
    exports chroniqor.core.replay;
    exports chroniqor.core.strategy;
}
