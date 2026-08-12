/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.market;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OHLC")
class OhlcTest {

    @Test
    @DisplayName("accepts open and close values inside the high-low range")
    void shouldAcceptValidValues() {
        new Ohlc(Price.of("1.1000"), Price.of("1.1200"), Price.of("1.0900"), Price.of("1.1100"));
    }

    @Test
    @DisplayName("rejects a high value below the low value")
    void shouldRejectHighBelowLow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Ohlc(Price.of("1.1000"), Price.of("1.0900"), Price.of("1.1000"), Price.of("1.1000")));
    }

    @Test
    @DisplayName("rejects an open value outside the high-low range")
    void shouldRejectOpenOutsideRange() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Ohlc(Price.of("1.1300"), Price.of("1.1200"), Price.of("1.0900"), Price.of("1.1100")));
    }

    @Test
    @DisplayName("rejects a close value outside the high-low range")
    void shouldRejectCloseOutsideRange() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Ohlc(Price.of("1.1000"), Price.of("1.1200"), Price.of("1.0900"), Price.of("1.1300")));
    }

    @Test
    @DisplayName("rejects null OHLC components")
    void shouldRejectNullComponents() {
        assertThrows(
                NullPointerException.class,
                () -> new Ohlc(null, Price.of("1.1200"), Price.of("1.0900"), Price.of("1.1100")));
        assertThrows(
                NullPointerException.class,
                () -> new Ohlc(Price.of("1.1000"), null, Price.of("1.0900"), Price.of("1.1100")));
        assertThrows(
                NullPointerException.class,
                () -> new Ohlc(Price.of("1.1000"), Price.of("1.1200"), null, Price.of("1.1100")));
        assertThrows(
                NullPointerException.class,
                () -> new Ohlc(Price.of("1.1000"), Price.of("1.1200"), Price.of("1.0900"), null));
    }
}
