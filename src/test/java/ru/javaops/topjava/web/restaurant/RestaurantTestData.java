package ru.javaops.topjava.web.restaurant;

import ru.javaops.topjava.MatcherFactory;
import ru.javaops.topjava.model.Dish;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.model.Vote;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.javaops.topjava.web.dish.DishTestData.*;
import static ru.javaops.topjava.web.user.UserTestData.user_2;
import static ru.javaops.topjava.web.voting.VoteTestData.HARBIN_VOTES_NOW;
import static ru.javaops.topjava.web.voting.VoteTestData.VOTE_ID;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 06.09.2021
 */
public class RestaurantTestData {

    public static final MatcherFactory.Matcher<Restaurant> RESTAURANT_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class, "votes", "dishes");


    public static MatcherFactory.Matcher<Restaurant> WITH_DISHES_MATCHER =
            MatcherFactory.usingRecurciveIgnoringFieldsComparator(Restaurant.class,
                    "dishes.restaurant", "dishes.date", "votes");


    public static MatcherFactory.Matcher<Restaurant> WITH_VOTES_DISHES_MATCHER =
            MatcherFactory.usingRecurciveIgnoringFieldsComparator(Restaurant.class,
                    "votes.restaurant", "votes.date", "votes.user", "dishes.restaurant", "dishes.date");

    public static MatcherFactory.Matcher<Restaurant> WITH_VOTES_DISHES_USER_MATCHER =
            MatcherFactory.usingRecurciveIgnoringFieldsComparator(Restaurant.class,
                    "votes.restaurant", "votes.date", "votes.user.roles",
                    "dishes.restaurant", "dishes.date");

    public static final int RESTAURAUNT_HARBIN_ID = 1;
    public static final Restaurant RESTAURANT_HARBIN = new Restaurant(RESTAURAUNT_HARBIN_ID, "Харбин");
    public static final Restaurant RESTAURANT_CI = new Restaurant(RESTAURAUNT_HARBIN_ID + 1, "Си");
    public static final Restaurant RESTAURANT_HANOY = new Restaurant(RESTAURAUNT_HARBIN_ID + 2, "Ханой");

    public static final List<Restaurant> RESTAURANTS = Stream.of(RESTAURANT_CI, RESTAURANT_HARBIN, RESTAURANT_HANOY)
            .sorted(Comparator.comparing(Restaurant::getName)).toList();


    static {
        RESTAURANT_HARBIN.setDishes(Set.of(dish1, dish2, dish3));
        RESTAURANT_HARBIN.setVotes(HARBIN_VOTES_NOW);
        RESTAURANT_CI.setDishes(Set.of(dish4, dish5));
        RESTAURANT_CI.setVotes(Set.of());
    }


    public static final Vote VOTE_USER_2_HARBIN_NOW = new Vote(VOTE_ID + 4, RESTAURANT_HARBIN, user_2, now());
    public static final Vote VOTE_USER_2_HANOY_NOW = new Vote(VOTE_ID + 4, RESTAURANT_HANOY, user_2, now());

    public static Restaurant getNew() {
        return new Restaurant(null, "Resturant_created");
    }

    public static Restaurant setUpdatedDish(Restaurant restaurant, Dish dish) {
        final Restaurant updated = new Restaurant(restaurant.getId(), restaurant.getName());
        updated.setDishes(Set.of(dish, dish2, dish3));
        return updated;

    }

    public static Restaurant getUpdated() {
        final Restaurant new_Harbin = new Restaurant(RESTAURAUNT_HARBIN_ID, "Харбин обновлен");
        new_Harbin.setVotes(HARBIN_VOTES_NOW);
        return new_Harbin;
    }

}
