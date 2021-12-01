package ru.ustinov.voting.web.restaurant;

import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.repository.RestaurantRepository;
import ru.ustinov.voting.web.GlobalExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 29.11.2021
 */

@Component
@AllArgsConstructor
public class UniqueRestaurantNameValidator implements Validator {

    private final RestaurantRepository repository;

    private final HttpServletRequest request;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return Restaurant.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        Restaurant restaurant = ((Restaurant) target);
        if (StringUtils.hasText(restaurant.getName())) {
            repository.getByName(restaurant.getName()).ifPresent(dbRestaurant -> {
                Assert.notNull(request, "HttpServletRequest missed");
                Integer restaurant_id = dbRestaurant.getId();
                if (request.getMethod().equals("PUT") || (request.getMethod().equals("POST"))) {
                    if ((restaurant.getId() != null && Objects.equals(restaurant.getId(), dbRestaurant.getId()))
                            || (request.getRequestURI().endsWith("restaurants/" + restaurant_id))) {
                        return;
                    }
                    if (restaurant.getName().equalsIgnoreCase(dbRestaurant.getName())) {
                        errors.rejectValue("name", GlobalExceptionHandler.EXCEPTION_DUPLICATE_RESTAURANT_NAME, new String[]{restaurant.getName()}, null);
                    }
                }
            });
        }
    }
}
