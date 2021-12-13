package ru.ustinov.voting.web.user;

import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.repository.UserRepository;
import ru.ustinov.voting.web.GlobalExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 13.12.2021
 */
@Component
@AllArgsConstructor
public class UniqueMailUserValidator implements org.springframework.validation.Validator {

    private final UserRepository repository;

    private final HttpServletRequest request;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        User user = ((User) target);
        if (StringUtils.hasText(user.getEmail())) {
            repository.getByEmail(user.getEmail())
                    .ifPresent(dbUser -> {
                        Assert.notNull(request, "HttpServletRequest missed");
                        if (request.getMethod().equals("PUT") || (request.getMethod().equals("POST"))) {
                            final Integer id = user.getId();
                            final int dbId = dbUser.id();
                            if (id != null && dbId == id || request.getRequestURI().endsWith("/" + dbId)) {
                                return;
                            }
                        }
                        errors.rejectValue("email", GlobalExceptionHandler.EXCEPTION_DUPLICATE_EMAIL);
                    });
        }
    }
}
