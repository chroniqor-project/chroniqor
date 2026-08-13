/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.dataset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import chroniqor.core.instrument.CurrencyCode;
import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import chroniqor.core.market.MarketBar;
import chroniqor.core.market.MarketEvent;
import chroniqor.core.market.Price;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Market dataset pipeline")
class MarketDatasetPipelineTest {

    private static final CurrencyPair EUR_USD = new CurrencyPair(new CurrencyCode("EUR"), new CurrencyCode("USD"));

    @Test
    @DisplayName("aggregates ordered market events and fingerprints the resulting dataset")
    void shouldBuildDatasetFromMarketEvents() {
        MarketEvent.Quote first = quote("2026-01-01T10:00:05Z", "1.1000", "1.1002", 100);
        MarketEvent.Quote second = quote("2026-01-01T10:00:30Z", "1.0998", "1.1001", 150);
        MarketEvent.Quote last = quote("2026-01-01T10:00:55Z", "1.1004", "1.1007", 200);

        MarketBar bar = MarketBar.fromQuotes(List.of(first, second, last), Timeframe.M1);
        MarketDataset dataset = MarketDataset.of("synthetic-eurusd", "1", List.of(bar));

        assertEquals(Instant.parse("2026-01-01T10:00:00Z"), bar.startTime());
        assertEquals(Instant.parse("2026-01-01T10:01:00Z"), bar.availableAt());
        assertEquals(Price.of("1.1000"), bar.bid().open());
        assertEquals(Price.of("1.1004"), bar.bid().close());
        assertEquals(Price.of("1.0998"), bar.bid().low());
        assertEquals(Price.of("1.1004"), bar.bid().high());
        assertEquals(Price.of("1.1007"), bar.ask().close());
        assertEquals(450, bar.tickVolume());
        assertEquals(bar, dataset.bars().getFirst());
        assertNotNull(dataset.identity().contentHash());
    }

    private static MarketEvent.Quote quote(String time, String bid, String ask, long tickVolume) {
        return new MarketEvent.Quote(EUR_USD, Instant.parse(time), Price.of(bid), Price.of(ask), tickVolume);
    }
}
