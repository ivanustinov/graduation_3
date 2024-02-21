package ru.ustinov.voting.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.ustinov.voting.error.AppException;
import ru.ustinov.voting.util.validation.ValidationUtil;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@Slf4j
@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String EXCEPTION_DUPLICATE_EMAIL = "exception.user.duplicateEmail";
    public static final String EXCEPTION_DUPLICATE_DISH_NAME = "dish.name_duplicate";
    public static final String EXCEPTION_DUPLICATE_RESTAURANT_NAME = "restaurant.name_duplicate";

    private final ErrorAttributes errorAttributes;

    private final MessageSourceAccessor messageSourceAccessor;

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatus status, @NonNull WebRequest request) {
        return handleBindingErrors(ex.getBindingResult(), request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        return handleBindingErrors(ex.getBindingResult(), request);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appException(WebRequest request, AppException ex) {
        log.error("ApplicationException", ex);
        final String messagCode = Objects.requireNonNull(ex.getReason());
        return createResponseEntity(getDefaultBody(request, ex.getOptions(),
                messageSourceAccessor.getMessage(messagCode, ex.getParams())), ex.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> persistException(WebRequest request, EntityNotFoundException ex) {
        log.error("EntityNotFoundException ", ex);
        final Throwable rootCause = ValidationUtil.getRootCause(ex);
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(MESSAGE),
                ValidationUtil.getMessage(rootCause)), HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(java.sql.SQLException.class)
    public ResponseEntity<?> persistException(WebRequest request, SQLException ex) {
        log.error("SQLException", ex);
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(MESSAGE), ex.getMessage()),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<Object> handleBindingErrors(BindingResult result, WebRequest request) {
        String[] msg = result.getAllErrors().stream().map(new Function<ObjectError, String>() {
            @Override
            public String apply(ObjectError resolvable) {
                return messageSourceAccessor.getMessage(resolvable);
            }
        }).toArray(String[]::new);
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.defaults(), msg), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private Map<String, Object> getDefaultBody(WebRequest request, ErrorAttributeOptions options, String... msg) {
        Map<String, Object> body = errorAttributes.getErrorAttributes(request, options);
        if (msg.length != 0) {
            body.put("message", msg);
        }
        return body;
    }

    @SuppressWarnings("unchecked")
    private <T> ResponseEntity<T> createResponseEntity(Map<String, Object> body, HttpStatus status) {
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        return (ResponseEntity<T>) ResponseEntity.status(status).body(body);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception ex, Object body, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        log.error("Exception", ex);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }
}
