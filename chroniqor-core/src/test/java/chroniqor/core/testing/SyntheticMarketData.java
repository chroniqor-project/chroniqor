/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.testing;

import chroniqor.core.instrument.CurrencyCode;
import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import chroniqor.core.market.MarketBar;
import chroniqor.core.market.Ohlc;
import chroniqor.core.market.Price;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class SyntheticMarketData {

    public static final CurrencyPair EUR_USD = new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD"));
    public static final Instant START = Instant.parse("2026-01-01T00:00:00Z");
    public static final int BAR_COUNT = 100;

    private static final BigDecimal PRICE_STEP = new BigDecimal("0.0001");
    private static final BigDecimal HIGH_OFFSET = new BigDecimal("0.0002");
    private static final BigDecimal LOW_OFFSET = new BigDecimal("0.0001");
    private static final BigDecimal ASK_OFFSET = new BigDecimal("0.0002");
    private static final BigDecimal INITIAL_BID = new BigDecimal("1.1000");

    private SyntheticMarketData() {}

    public static List<MarketBar> bars() {
        List<MarketBar> bars = new ArrayList<>(BAR_COUNT);

        for (int index = 0; index < BAR_COUNT; index++) {
            BigDecimal open = INITIAL_BID.add(PRICE_STEP.multiply(BigDecimal.valueOf(index)));
            BigDecimal high = open.add(HIGH_OFFSET);
            BigDecimal low = open.subtract(LOW_OFFSET);
            BigDecimal close = open.add(PRICE_STEP);
            Instant startTime = START.plusSeconds(Timeframe.M1.duration().toSeconds() * index);

            bars.add(new MarketBar(
                    EUR_USD,
                    Timeframe.M1,
                    startTime,
                    ohlc(open, high, low, close),
                    ohlc(open.add(ASK_OFFSET), high.add(ASK_OFFSET), low.add(ASK_OFFSET), close.add(ASK_OFFSET)),
                    1_000L + index));
        }

        return List.copyOf(bars);
    }

    private static Ohlc ohlc(BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close) {
        return new Ohlc(new Price(open), new Price(high), new Price(low), new Price(close));
    }
}
