package ru.ustinov.voting.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 10.12.2021
 */
public class UpdateRestrictionException extends AppException {

    public static final String EXCEPTION_UPDATE_RESTRICTION = "exception.user.updateRestriction";

    public UpdateRestrictionException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, ErrorAttributeOptions.of(MESSAGE), EXCEPTION_UPDATE_RESTRICTION);
    }
}
