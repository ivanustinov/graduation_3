package ru.ustinov.voting.web.restaurant;

import ru.ustinov.voting.MatcherFactory;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.to.RestaurantTo;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.now;
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

    public static final int NOT_FOUND = 100;

    public static final Restaurant RESTAURANT_HARBIN = new Restaurant(RESTAURAUNT_HARBIN_ID, "Харбин");

    public static final Restaurant RESTAURANT_CI = new Restaurant(RESTAURAUNT_HARBIN_ID + 1, "Си");

    public static final Restaurant RESTAURANT_HANOY = new Restaurant(RESTAURAUNT_HARBIN_ID + 2, "Ханой");

    public static final RestaurantTo RESTAURANT_HARBIN_TO;

    public static final RestaurantTo RESTAURANT_CI_TO = new RestaurantTo(RESTAURANT_CI, 0);

    public static final List<Restaurant> RESTAURANTS = Stream.of(RESTAURANT_CI, RESTAURANT_HARBIN, RESTAURANT_HANOY)
            .sorted(Comparator.comparing(Restaurant::getName)).toList();

    public static final Vote VOTE_USER_2_HARBIN_NOW = new Vote(VOTE_ID + 4, RESTAURANT_HARBIN, user_2, now());

    public static final Vote VOTE_USER_2_HANOY_NOW = new Vote(VOTE_ID + 4, RESTAURANT_HANOY, user_2, now());

    public static final int DISH_ID = 1;

    final static LocalDate now = now();

    public static final Dish dish1 = new Dish(DISH_ID, "Харчо", now, (valueOf(300.23)), RESTAURANT_HARBIN);
    public static final Dish dish2 = new Dish(DISH_ID + 1, "Жаркое", now, (valueOf(356.45)), RESTAURANT_HARBIN);
    public static final Dish dish3 = new Dish(DISH_ID + 2, "Компот", now, (valueOf(30.67)), RESTAURANT_HARBIN);
    public static final Dish dish4 = new Dish(DISH_ID + 3, "Сельдь под шубой", now, (valueOf(176.47)), RESTAURANT_CI);

    public static final Dish dish5 = new Dish(DISH_ID + 4, "Пиво", now, (valueOf(150.89)), RESTAURANT_CI);
    public static final Dish dish6 = new Dish(null, "Жаркое", now, (valueOf(260)), RESTAURANT_HARBIN);
    public static final Dish dish7 = new Dish(null, "Пюре картофельное с голубцами", now, (valueOf(225.78)), RESTAURANT_HARBIN);
    public static final Dish dish8 = new Dish(null, "Кофе с круассаном", now, (valueOf(100.35)), RESTAURANT_HARBIN);

    public static List<Dish> harbinDishesNow = Stream.of(dish2, dish3, dish1).sorted(Comparator.comparing(Dish::getName)).toList();
    public static List<Dish> ciDishesNow = Stream.of(dish4, dish5).sorted(Comparator.comparing(Dish::getName)).toList();

    public static List<Dish> harbinDishesInThePastWithDuplicate = List.of(dish6, dish7, dish8);

    public static List<Dish> harbinNewDishesFromLastMenu = List.copyOf(harbinDishesNow);

    public static final List<Dish> newHarbinDishesFromLastMenu;


    static {
        newHarbinDishesFromLastMenu = harbinNewDishesFromLastMenu.stream().map(dish -> {
            Dish newDish = new Dish(dish);
            newDish.setId(null);
            newDish.setDate(now.plusDays(1));
            return newDish;
        }).toList();
        RESTAURANT_HARBIN.setDishes(harbinDishesNow);
        RESTAURANT_CI.setDishes(ciDishesNow);
        RESTAURANT_HARBIN_TO = new RestaurantTo(RESTAURANT_HARBIN, 2);
    }

    public static Dish getNewDish() {
        return new Dish(null, "Созданное блюдо", now, (valueOf(200.34)), RESTAURANT_HARBIN);
    }

    public static Dish getUpdatedDish() {
        return new Dish(DISH_ID, "Обновленнoe харчо", now, (valueOf(500)), RESTAURANT_HARBIN);
    }


    public static Restaurant getNew() {
        return new Restaurant(null, "Restaurant_created");
    }

    public static Restaurant setUpdatedDish(Restaurant restaurant, Dish dish) {
        final Restaurant updated = new Restaurant(restaurant);
        updated.setDishes(Stream.of(dish, dish2, dish3).sorted(Comparator.comparing(Dish::getName)).toList());
        return updated;
    }

    public static Restaurant getUpdated() {
        final Restaurant new_Harbin = new Restaurant(RESTAURANT_HARBIN);
        new_Harbin.setName("Харбин обновлен");
        new_Harbin.setDishes(harbinDishesNow);
        return new_Harbin;
    }

}
