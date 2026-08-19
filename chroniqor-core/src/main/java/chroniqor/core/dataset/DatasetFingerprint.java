/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.dataset;

import chroniqor.core.instrument.CurrencyPair;
import chroniqor.core.instrument.Timeframe;
import chroniqor.core.market.MarketBar;
import chroniqor.core.market.Price;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

final class DatasetFingerprint {

    private static final String SCHEMA = "chroniqor-market-dataset-v1";

    private DatasetFingerprint() {}

    static String sha256(CurrencyPair instrument, Timeframe timeframe, List<MarketBar> bars) {
        MessageDigest digest = sha256Digest();

        update(digest, SCHEMA);

        update(digest, "instrument=" + instrument.symbol());

        update(digest, "timeframe=" + timeframe.name());

        update(digest, "barCount=" + bars.size());

        for (MarketBar bar : bars) {
            update(digest, canonicalize(bar));
        }

        return HexFormat.of().formatHex(digest.digest());
    }

    private static String canonicalize(MarketBar bar) {
        return String.join(
                "|",
                bar.startTime().toString(),
                price(bar.bid().open()),
                price(bar.bid().high()),
                price(bar.bid().low()),
                price(bar.bid().close()),
                price(bar.ask().open()),
                price(bar.ask().high()),
                price(bar.ask().low()),
                price(bar.ask().close()),
                Long.toString(bar.tickVolume()));
    }

    private static String price(Price price) {
        return price.value().stripTrailingZeros().toPlainString();
    }

    private static void update(MessageDigest digest, String value) {
        digest.update(value.getBytes(StandardCharsets.UTF_8));

        digest.update((byte) '\n');
    }

    private static MessageDigest sha256Digest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
