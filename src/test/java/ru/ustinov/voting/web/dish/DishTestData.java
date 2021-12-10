package ru.ustinov.voting.web.dish;

import ru.ustinov.voting.MatcherFactory;
import ru.ustinov.voting.model.Dish;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.now;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.RESTAURANT_CI;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.RESTAURANT_HARBIN;

public class DishTestData {

    public static final MatcherFactory.Matcher<Dish> MATCHER = MatcherFactory
            .usingIgnoringFieldsComparator(Dish.class, "restaurant.name", "restaurant.dishes");

    public static final MatcherFactory.Matcher<Dish> MATCHER_WITHOUT_ID_RESTAURANT = MatcherFactory
            .usingIgnoringFieldsComparator(Dish.class, "id", "restaurant");


}
