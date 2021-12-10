package ru.ustinov.voting.error;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;

@Getter
public class AppException extends ResponseStatusException {

    private final ErrorAttributeOptions options;

    private String[] params;

    public AppException(HttpStatus status, ErrorAttributeOptions options, String message,  String ... params) {
        super(status, message);
        this.options = options;
        this.params = params;
    }

    public AppException(HttpStatus status,  ErrorAttributeOptions options, String message) {
        super(status,message);
        this.options = options;
    }


}
