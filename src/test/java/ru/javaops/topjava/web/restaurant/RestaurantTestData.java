package ru.javaops.topjava.web.restaurant;

import ru.javaops.topjava.MatcherFactory;
import ru.javaops.topjava.model.Dish;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.model.Vote;
import ru.javaops.topjava.to.RestaurantTo;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
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
            MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class, "");


    public static MatcherFactory.Matcher<RestaurantTo> WITH_DISHES_MATCHER =
            MatcherFactory.usingRecurciveIgnoringFieldsComparator(RestaurantTo.class,
                    "dishes.restaurant", "dishes.date", "votes");


    public static MatcherFactory.Matcher<RestaurantTo> WITH_VOTES_DISHES_MATCHER =
            MatcherFactory.usingRecurciveIgnoringFieldsComparator(RestaurantTo.class,
                    "votes.restaurant", "votes.date", "votes.user", "dishes.restaurant", "dishes.date");

    public static MatcherFactory.Matcher<RestaurantTo> WITH_VOTES_DISHES_USER_MATCHER =
            MatcherFactory.usingRecurciveIgnoringFieldsComparator(RestaurantTo.class,
                    "votes.restaurant", "votes.date", "votes.user.roles", "votes.user.votes",
                    "dishes.restaurant", "dishes.date");

    public static final int RESTAURAUNT_HARBIN_ID = 1;
    public static final Restaurant RESTAURANT_HARBIN = new Restaurant(RESTAURAUNT_HARBIN_ID, "Харбин");
    public static final Restaurant RESTAURANT_CI = new Restaurant(RESTAURAUNT_HARBIN_ID + 1, "Си");
    public static final Restaurant RESTAURANT_HANOY = new Restaurant(RESTAURAUNT_HARBIN_ID + 2, "Ханой");

    public static final RestaurantTo RESTAURANT_HARBIN_TO = new RestaurantTo(RESTAURANT_HARBIN);
    public static final RestaurantTo RESTAURANT_CI_TO = new RestaurantTo(RESTAURANT_CI);
    public static final RestaurantTo RESTAURANT_HANOY_TO = new RestaurantTo(RESTAURANT_HANOY);


    public static final List<Restaurant> RESTAURANTS = Stream.of(RESTAURANT_CI, RESTAURANT_HARBIN, RESTAURANT_HANOY)
            .sorted(Comparator.comparing(Restaurant::getName)).toList();



    public static void setVotesAndDishes(RestaurantTo restaurantTo, List<Dish> dishes, List<Vote> votes) {
        restaurantTo.setDishes(dishes);
        restaurantTo.setVotes(votes);
    }


    public static final Vote VOTE_USER_2_HARBIN_NOW = new Vote(VOTE_ID + 4, RESTAURANT_HARBIN, user_2, now());
    public static final Vote VOTE_USER_2_HANOY_NOW = new Vote(VOTE_ID + 4, RESTAURANT_HANOY, user_2, now());

    public static Restaurant getNew() {
        return new Restaurant(null, "Restaurant_created");
    }

    public static RestaurantTo setUpdatedDish(Restaurant restaurant, Dish dish) {
        final RestaurantTo updated = new RestaurantTo(restaurant);
        updated.setDishes(Stream.of(dish, dish2, dish3).sorted(Comparator.comparing(Dish::getName)).toList());
        return updated;
    }

    public static RestaurantTo getUpdatedTo() {
        final RestaurantTo new_Harbin = new RestaurantTo(RESTAURANT_HARBIN);
        new_Harbin.setName("Харбин обновлен");
        setVotesAndDishes(new_Harbin, harbinDishesNow, HARBIN_VOTES_NOW);
        return new_Harbin;
    }
    public static Restaurant getUpdated() {
        final Restaurant new_Harbin = new Restaurant(RESTAURANT_HARBIN);
        new_Harbin.setName("Харбин обновлен");
        return new_Harbin;
    }

}
