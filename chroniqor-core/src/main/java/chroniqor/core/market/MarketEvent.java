/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.market;

import chroniqor.core.instrument.CurrencyPair;
import java.time.Instant;

public sealed interface MarketEvent permits MarketBar {

    CurrencyPair instrument();

    Instant availableAt();
}
