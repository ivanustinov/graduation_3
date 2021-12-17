package ru.ustinov.voting.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.ustinov.voting.model.Restaurant;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 21.10.2021
 */
@Data
@EqualsAndHashCode
@AllArgsConstructor
public class MenusTo {

    private List<Restaurant> restaurants;

    private LocalDate date;
}
