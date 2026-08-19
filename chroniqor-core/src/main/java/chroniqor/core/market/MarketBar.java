/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.market;

import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Immutable bid/ask OHLC bar aligned to a supported timeframe.
 *
 * <p>The bar becomes available at its end time. This distinction is central
 * to replay: a strategy cannot observe the bar before its full interval has
 * elapsed in market time.
 *
 * @param instrument bar instrument
 * @param timeframe bar duration and alignment
 * @param startTime start of the bar interval
 * @param bid bid-side OHLC values
 * @param ask ask-side OHLC values, pointwise not below bid
 * @param tickVolume non-negative aggregated tick volume
 */
public record MarketBar(
        CurrencyPair instrument, Timeframe timeframe, Instant startTime, Ohlc bid, Ohlc ask, long tickVolume)
        implements MarketEvent {

    /**
     * Creates a bar with a default tick volume of zero.
     *
     * @param instrument bar instrument
     * @param timeframe bar duration and alignment
     * @param startTime start of the bar interval
     * @param bid bid-side OHLC values
     * @param ask ask-side OHLC values
     * @throws IllegalArgumentException if the bar invariants are violated
     * @throws NullPointerException if an argument is null
     */
    public MarketBar(CurrencyPair instrument, Timeframe timeframe, Instant startTime, Ohlc bid, Ohlc ask) {
        this(instrument, timeframe, startTime, bid, ask, 0);
    }

    /**
     * Validates alignment, volume and pointwise bid/ask spread invariants.
     *
     * @param instrument bar instrument
     * @param timeframe bar timeframe
     * @param startTime bar start time
     * @param bid bid OHLC values
     * @param ask ask OHLC values
     * @param tickVolume non-negative tick volume
     * @throws IllegalArgumentException if tick volume is negative, the start
     *     time is not aligned, or an ask price is below its bid
     * @throws NullPointerException if an argument is null
     */
    public MarketBar {
        Objects.requireNonNull(instrument, "Instrument must not be null");
        Objects.requireNonNull(timeframe, "Timeframe must not be null");
        Objects.requireNonNull(startTime, "Start time must not be null");
        Objects.requireNonNull(bid, "Bid OHLC must not be null");
        Objects.requireNonNull(ask, "Ask OHLC must not be null");

        if (tickVolume < 0) {
            throw new IllegalArgumentException("Bar tick volume must not be negative");
        }

        if (!isAligned(startTime, timeframe)) {
            throw new IllegalArgumentException("Bar start time must be aligned to its timeframe");
        }

        requireAskNotBelowBid(bid.open(), ask.open(), "open");
        requireAskNotBelowBid(bid.high(), ask.high(), "high");
        requireAskNotBelowBid(bid.low(), ask.low(), "low");
        requireAskNotBelowBid(bid.close(), ask.close(), "close");
    }

    /**
     * Returns the exclusive end of the bar interval.
     *
     * @return start time plus the timeframe duration
     */
    public Instant endTime() {
        return startTime.plus(timeframe.duration());
    }

    @Override
    /**
     * Returns when the completed bar becomes visible to a strategy.
     *
     * @return bar end time
     */
    public Instant availableAt() {
        return endTime();
    }

    /**
     * Aggregates quotes from one timeframe bucket into a bid/ask OHLC bar.
     *
     * @param quotes non-empty, strictly time-ordered quotes from one bucket
     * @param timeframe target bar timeframe
     * @return aggregated market bar
     * @throws IllegalArgumentException if quotes are empty, unordered, span
     *     instruments or timeframe buckets, or tick volume overflows
     * @throws NullPointerException if an argument or quote is null
     */
    public static MarketBar fromQuotes(List<MarketEvent.Quote> quotes, Timeframe timeframe) {
        Objects.requireNonNull(quotes, "Market quotes must not be null");
        Objects.requireNonNull(timeframe, "Timeframe must not be null");

        if (quotes.isEmpty()) {
            throw new IllegalArgumentException("At least one market quote is required");
        }

        MarketEvent.Quote first = Objects.requireNonNull(quotes.getFirst(), "Market quote must not be null");
        CurrencyPair instrument = first.instrument();
        Instant startTime = bucketStart(first.availableAt(), timeframe);
        Price bidOpen = first.bid();
        Price bidHigh = first.bid();
        Price bidLow = first.bid();
        Price bidClose = first.bid();
        Price askOpen = first.ask();
        Price askHigh = first.ask();
        Price askLow = first.ask();
        Price askClose = first.ask();
        long tickVolume = first.tickVolume();
        Instant previousTime = first.availableAt();

        for (int i = 1; i < quotes.size(); i++) {
            MarketEvent.Quote current = Objects.requireNonNull(quotes.get(i), "Market quote must not be null");

            if (!current.instrument().equals(instrument)) {
                throw new IllegalArgumentException("All market events must belong to the same instrument");
            }

            if (!current.availableAt().isAfter(previousTime)) {
                throw new IllegalArgumentException("Market events must be strictly ordered by event time");
            }

            if (!bucketStart(current.availableAt(), timeframe).equals(startTime)) {
                throw new IllegalArgumentException("All market events must belong to the same timeframe interval");
            }

            bidHigh = max(bidHigh, current.bid());
            bidLow = min(bidLow, current.bid());
            bidClose = current.bid();
            askHigh = max(askHigh, current.ask());
            askLow = min(askLow, current.ask());
            askClose = current.ask();
            tickVolume = Math.addExact(tickVolume, current.tickVolume());
            previousTime = current.availableAt();
        }

        return new MarketBar(
                instrument,
                timeframe,
                startTime,
                new Ohlc(bidOpen, bidHigh, bidLow, bidClose),
                new Ohlc(askOpen, askHigh, askLow, askClose),
                tickVolume);
    }

    private static Instant bucketStart(Instant instant, Timeframe timeframe) {
        long durationSeconds = timeframe.duration().toSeconds();
        long bucket = Math.floorDiv(instant.getEpochSecond(), durationSeconds) * durationSeconds;
        return Instant.ofEpochSecond(bucket);
    }

    private static boolean isAligned(Instant instant, Timeframe timeframe) {
        return instant.getNano() == 0
                && Math.floorMod(instant.getEpochSecond(), timeframe.duration().toSeconds()) == 0;
    }

    private static Price max(Price first, Price second) {
        return first.compareTo(second) >= 0 ? first : second;
    }

    private static Price min(Price first, Price second) {
        return first.compareTo(second) <= 0 ? first : second;
    }

    private static void requireAskNotBelowBid(Price bid, Price ask, String point) {
        if (ask.compareTo(bid) < 0) {
            throw new IllegalArgumentException(
                    "Ask " + point + " price must not be lower than bid " + point + " price");
        }
    }
}
