package ru.ustinov.voting.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

public class IllegalRequestDataException extends AppException {

    public IllegalRequestDataException(String msg, String... params) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, ErrorAttributeOptions.of(MESSAGE), msg, params);
    }
}