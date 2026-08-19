/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.market;

import chroniqor.core.instrument.CurrencyPair;
import java.time.Instant;
import java.util.Objects;

/**
 * Market information with an instrument and an explicit availability time.
 *
 * <p>The sealed hierarchy currently contains aggregated {@link MarketBar}
 * values and raw {@link Quote} values.
 */
public sealed interface MarketEvent permits MarketBar, MarketEvent.Quote {

    /**
     * Returns the instrument represented by this event.
     *
     * @return event instrument
     */
    CurrencyPair instrument();

    /**
     * Returns the domain time at which this event may be consumed.
     *
     * @return explicit market availability time
     */
    Instant availableAt();

    /**
     * A single bid/ask quote with positive tick volume.
     *
     * @param instrument quote instrument
     * @param availableAt explicit availability time
     * @param bid bid price
     * @param ask ask price
     * @param tickVolume positive tick volume
     */
    record Quote(CurrencyPair instrument, Instant availableAt, Price bid, Price ask, long tickVolume)
            implements MarketEvent {

        /**
         * Creates a quote with a default tick volume of one.
         *
         * @param instrument quote instrument
         * @param availableAt explicit quote availability time
         * @param bid bid price
         * @param ask ask price, not below bid
         * @throws IllegalArgumentException if ask is below bid
         * @throws NullPointerException if an argument is null
         */
        public Quote(CurrencyPair instrument, Instant availableAt, Price bid, Price ask) {
            this(instrument, availableAt, bid, ask, 1L);
        }

        /**
         * Validates quote values and the bid/ask ordering invariant.
         *
         * @param instrument quote instrument
         * @param availableAt explicit quote availability time
         * @param bid bid price
         * @param ask ask price, not below bid
         * @param tickVolume positive quote tick volume
         * @throws IllegalArgumentException if ask is below bid or tick volume
         *     is not positive
         * @throws NullPointerException if an argument is null
         */
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
