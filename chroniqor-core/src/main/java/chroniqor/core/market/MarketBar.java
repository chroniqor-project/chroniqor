/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.market;

import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import java.time.Instant;
import java.util.Objects;

public record MarketBar(CurrencyPair instrument, Timeframe timeframe, Instant startTime, Ohlc bid, Ohlc ask)
        implements MarketEvent {

    public MarketBar {
        Objects.requireNonNull(instrument, "Instrument must not be null");
        Objects.requireNonNull(timeframe, "Timeframe must not be null");
        Objects.requireNonNull(startTime, "Start time must not be null");
        Objects.requireNonNull(bid, "Bid OHLC must not be null");
        Objects.requireNonNull(ask, "Ask OHLC must not be null");

        requireAskNotBelowBid(bid.open(), ask.open(), "open");
        requireAskNotBelowBid(bid.high(), ask.high(), "high");
        requireAskNotBelowBid(bid.low(), ask.low(), "low");
        requireAskNotBelowBid(bid.close(), ask.close(), "close");
    }

    public Instant endTime() {
        return startTime.plus(timeframe.duration());
    }

    @Override
    public Instant availableAt() {
        return endTime();
    }

    private static void requireAskNotBelowBid(Price bid, Price ask, String point) {
        if (ask.compareTo(bid) < 0) {
            throw new IllegalArgumentException(
                    "Ask " + point + " price must not be lower than bid " + point + " price");
        }
    }
}
