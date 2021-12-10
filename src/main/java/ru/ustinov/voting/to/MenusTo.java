package ru.ustinov.voting.to;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 21.10.2021
 */
@Data
@EqualsAndHashCode
@AllArgsConstructor
public class MenusTo {

    private List<String> restaurants;

    private LocalDate date;
}
