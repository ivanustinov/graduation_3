package ru.ustinov.voting.web.formatter;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 09.12.2021
 */
class PriceFormatterTest {

    @Test
    void formatPrice() {
        final BigDecimal endZero = new BigDecimal("23.10");
        final BigDecimal noEndZero = new BigDecimal("23.00");
        final String withTrailingZero = PriceFormatter.formatPrice(endZero);
        final String withoutTrailingZero = PriceFormatter.formatPrice(noEndZero);
        assertEquals("23.10", withTrailingZero);
        assertEquals("23", withoutTrailingZero);
    }
}