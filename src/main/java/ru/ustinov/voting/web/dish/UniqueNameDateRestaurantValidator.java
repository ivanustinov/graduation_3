package ru.ustinov.voting.web.dish;

import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.web.GlobalExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 28.11.2021
 */

@Component
@AllArgsConstructor
public class UniqueNameDateRestaurantValidator implements Validator {

    private final DishRepository dishRepository;

    private final HttpServletRequest request;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return Dish.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        Dish dish = ((Dish) target);
        if (StringUtils.hasText(dish.getName())) {
            final Integer restaurant_id = getRestaurantId();
            dishRepository.getDishByDateAndNameAndRestaurant(dish.getDate(), dish.getName(), restaurant_id).ifPresent(dbDish -> {
                Assert.notNull(request, "HttpServletRequest missed");
                if (request.getMethod().equals("PUT") || (request.getMethod().equals("POST"))) {
                    if ((dish.getId() != null && Objects.equals(dish.getId(), dbDish.getId())) || request.getRequestURI().endsWith("dishes/" + restaurant_id + "/" + dbDish.getId())) {
                        return;
                    }
                    if (request.getRequestURI().contains("dishes/" + restaurant_id)) {
                        if (dish.getName().equalsIgnoreCase(dbDish.getName()) && dish.getDate().equals(dbDish.getDate())) {
                            errors.rejectValue("name", GlobalExceptionHandler.EXCEPTION_DUPLICATE_DISH_NAME, new String[]{dish.getName()}, null);
                        }
                    }
                }
            });
        }
    }

    private Integer getRestaurantId() {
        final String requestURI = request.getRequestURI();
        final String[] split = requestURI.split("dishes");
        final String[] split1 = split[1].split("/");
        final String r = split1[1];
        return Objects.requireNonNull(Integer.valueOf(r));
    }
}

