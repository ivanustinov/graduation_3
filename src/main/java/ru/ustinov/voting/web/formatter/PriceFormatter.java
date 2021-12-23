package ru.ustinov.voting.web.formatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 09.12.2021
 */
public class PriceFormatter extends JsonSerializer<BigDecimal> {

    public static String formatPrice(BigDecimal price) {
        Pattern pattern = Pattern.compile("\\.{1}[1-9]");
        Matcher matcher = pattern.matcher(price.toString());
        if (matcher.find()) {
            return price.toString();
        } else return price.stripTrailingZeros().toPlainString();
    }

    @Override
    public void serialize(BigDecimal price, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        Pattern pattern = Pattern.compile("\\.{1}[1-9]{1}");
        Matcher matcher = pattern.matcher(price.toString());
        String formattedPrice;
        if (matcher.find()) {
            formattedPrice = price.toString();
        } else formattedPrice = price.stripTrailingZeros().toPlainString();
        gen.writeString(formattedPrice);
    }
}
