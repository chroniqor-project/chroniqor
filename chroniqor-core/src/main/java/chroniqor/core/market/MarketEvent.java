/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.market;

import chroniqor.core.instrument.CurrencyPair;
import java.time.Instant;
import java.util.Objects;

public sealed interface MarketEvent permits MarketBar, MarketEvent.Quote {

    CurrencyPair instrument();

    Instant availableAt();

    record Quote(CurrencyPair instrument, Instant availableAt, Price bid, Price ask, long tickVolume)
            implements MarketEvent {

        public Quote(CurrencyPair instrument, Instant availableAt, Price bid, Price ask) {
            this(instrument, availableAt, bid, ask, 1L);
        }

        public Quote {
            Objects.requireNonNull(instrument, "Instrument must not be null");
            Objects.requireNonNull(availableAt, "Event time must not be null");
            Objects.requireNonNull(bid, "Bid price must not be null");
            Objects.requireNonNull(ask, "Ask price must not be null");

            if (ask.compareTo(bid) < 0) {
                throw new IllegalArgumentException("Ask price must not be lower than bid price");
            }

            if (tickVolume <= 0) {
                throw new IllegalArgumentException("Quote tick volume must be greater than zero");
            }
        }
    }
}
