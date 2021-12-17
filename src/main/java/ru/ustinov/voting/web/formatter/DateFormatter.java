package ru.ustinov.voting.web.formatter;

import lombok.Data;
import org.springframework.core.env.Environment;
import ru.ustinov.voting.Profiles;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 05.12.2021
 */

@Data
public class DateFormatter {

    private static final String RU_DATE_FORMAT = "dd.MM.yyyy";

    private static final String ISO_DATE_FORMAT = "yyyy-MM-dd";

    public static String STATIC_ACTIVE_DATE_FORMAT = ISO_DATE_FORMAT;

    private Environment environment;

    public DateFormatter(Environment environment) {
        this.environment = environment;
    }

    public static String format(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(STATIC_ACTIVE_DATE_FORMAT));
    }

    @PostConstruct
    private void postConstr() {
        if (environment.acceptsProfiles(Profiles.RU_DATE_FORMAT)) {
            STATIC_ACTIVE_DATE_FORMAT = RU_DATE_FORMAT;
        }
    }
}
