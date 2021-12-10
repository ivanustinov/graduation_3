package ru.ustinov.voting.util.validation;

import ru.ustinov.voting.error.NotFoundException;

import java.util.Optional;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 28.09.2021
 */
public class  Util {

    public static <T> T getEntity(Optional<T> opt, String msg, String ... params) {
         return opt.orElseThrow(() -> new NotFoundException(msg, params));
    }

}
