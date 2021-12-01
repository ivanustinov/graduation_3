package ru.ustinov.voting.web.restaurant;

import ru.ustinov.voting.MatcherFactory;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.to.RestaurantTo;
import ru.ustinov.voting.web.dish.DishTestData;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
import static ru.ustinov.voting.web.dish.DishTestData.*;
import static ru.ustinov.voting.web.user.UserTestData.user_2;
import static ru.ustinov.voting.web.voting.VoteTestData.HARBIN_VOTES_NOW;
import static ru.ustinov.voting.web.voting.VoteTestData.VOTE_ID;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 06.09.2021
 */
public class RestaurantTestData {

    public static final MatcherFactory.Matcher<Restaurant> RESTAURANT_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class, "dishes");

    public static MatcherFactory.Matcher<Restaurant> WITH_DISHES_MATCHER =
            MatcherFactory.usingRecurciveIgnoringFieldsComparator(Restaurant.class,
                    "dishes.restaurant", "dishes.date");

    public static MatcherFactory.Matcher<RestaurantTo> TO_MATCHER =
            MatcherFactory.usingRecurciveIgnoringFieldsComparator(RestaurantTo.class,
                     "dishes.restaurant", "dishes.date");

    public static final int RESTAURAUNT_HARBIN_ID = 1;

    public static final int RESTAURAUNT_NOT_FOUND_ID = 100;

    public static final Restaurant RESTAURANT_HARBIN = new Restaurant(RESTAURAUNT_HARBIN_ID, "Харбин", harbinDishesNow);

    public static final Restaurant RESTAURANT_CI = new Restaurant(RESTAURAUNT_HARBIN_ID + 1, "Си", ciDishesNow);

    public static final Restaurant RESTAURANT_HANOY = new Restaurant(RESTAURAUNT_HARBIN_ID + 2, "Ханой");

    public static final RestaurantTo RESTAURANT_HARBIN_TO = new RestaurantTo(RESTAURANT_HARBIN, 2);

    public static final RestaurantTo RESTAURANT_CI_TO = new RestaurantTo(RESTAURANT_CI, 0);

    public static final List<Restaurant> RESTAURANTS = Stream.of(RESTAURANT_CI, RESTAURANT_HARBIN, RESTAURANT_HANOY)
            .sorted(Comparator.comparing(Restaurant::getName)).toList();

    public static final Vote VOTE_USER_2_HARBIN_NOW = new Vote(VOTE_ID + 4, RESTAURANT_HARBIN, user_2, now());

    public static final Vote VOTE_USER_2_HANOY_NOW = new Vote(VOTE_ID + 4, RESTAURANT_HANOY, user_2, now());

    public static Restaurant getNew() {
        return new Restaurant(null, "Restaurant_created");
    }

    public static Restaurant setUpdatedDish(Restaurant restaurant, Dish dish) {
        final Restaurant updated = new Restaurant(restaurant);
        updated.setDishes(Stream.of(dish, dish2, dish3).sorted(Comparator.comparing(Dish::getName)).toList());
        return updated;
    }

    public static RestaurantTo getUpdatedTo() {
        final RestaurantTo new_Harbin = new RestaurantTo(RESTAURANT_HARBIN, HARBIN_VOTES_NOW.size());
        new_Harbin.setName("Харбин обновлен");
        return new_Harbin;
    }

    public static Restaurant getUpdated() {
        final Restaurant new_Harbin = new Restaurant(RESTAURANT_HARBIN);
        new_Harbin.setName("Харбин обновлен");
        new_Harbin.setDishes(harbinDishesNow);
        return new_Harbin;
    }

}
