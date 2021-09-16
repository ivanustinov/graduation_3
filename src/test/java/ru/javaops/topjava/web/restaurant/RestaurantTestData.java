package ru.javaops.topjava.web.restaurant;

import ru.javaops.topjava.MatcherFactory;
import ru.javaops.topjava.model.Dish;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.model.Vote;

import java.time.Month;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.now;
import static java.time.LocalDate.of;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.javaops.topjava.web.user.UserTestData.user_2;
import static ru.javaops.topjava.web.voting.VoteTestData.HARBIN_VOTES_NOW;
import static ru.javaops.topjava.web.voting.VoteTestData.VOTE_ID;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 06.09.2021
 */
public class RestaurantTestData {

    public static final MatcherFactory.Matcher<Restaurant> RESTAURANT_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class, "votes", "dishes");


    public static MatcherFactory.Matcher<Restaurant> WITH_DISHES_MATCHER =
            MatcherFactory.usingAssertions(Restaurant.class,
//     No need use ignoringAllOverriddenEquals, see https://assertj.github.io/doc/#breaking-changes
                    (a, e) -> assertThat(a).usingRecursiveComparison()
                            .ignoringFields("dishes.restaurant", "dishes.date", "votes").isEqualTo(e),
                    (a, e) -> {
                        final Iterator<Restaurant> iteratorE = e.iterator();
                        final Iterator<Restaurant> iteratorA = a.iterator();
                        while (iteratorA.hasNext() || iteratorE.hasNext()) {
                            final Restaurant A = iteratorA.next();
                            final Restaurant E = iteratorE.next();
                            assertThat(A).usingRecursiveComparison().ignoringFields("dishes.restaurant", "dishes.date", "votes").isEqualTo(E);
                        }
                    });

    public static MatcherFactory.Matcher<Restaurant> WITH_VOTES_DISHES_MATCHER =
            MatcherFactory.usingAssertions(Restaurant.class,
//     No need use ignoringAllOverriddenEquals, see https://assertj.github.io/doc/#breaking-changes
                    (a, e) -> assertThat(a).usingRecursiveComparison()
                            .ignoringFields("votes.restaurant", "votes.date",
                                    "votes.user", "dishes.restaurant", "dishes.date").isEqualTo(e),
                    (a, e) -> {
                        final Iterator<Restaurant> iteratorE = e.iterator();
                        final Iterator<Restaurant> iteratorA = a.iterator();
                        while (iteratorA.hasNext() || iteratorE.hasNext()) {
                            final Restaurant A = iteratorA.next();
                            final Restaurant E = iteratorE.next();
                            assertThat(A).usingRecursiveComparison().ignoringFields("votes.restaurant", "votes.date",
                                    "votes.user",
                                    "dishes.restaurant", "dishes.date").isEqualTo(E);
                        }
                    });

    public static final int RESTAURAUNT_HARBIN_ID = 1;
    public static final Restaurant RESTAURANT_HARBIN = new Restaurant(RESTAURAUNT_HARBIN_ID, "Харбин");
    public static final Restaurant RESTAURANT_CI = new Restaurant(RESTAURAUNT_HARBIN_ID + 1, "Си");
    public static final Restaurant RESTAURANT_HANOY = new Restaurant(RESTAURAUNT_HARBIN_ID + 2, "Ханой");

    public static final List<Restaurant> RESTAURANTS = Stream.of(RESTAURANT_CI, RESTAURANT_HARBIN, RESTAURANT_HANOY)
            .sorted(Comparator.comparing(Restaurant::getName)).toList();

    public static final int DISH_ID = 1;
    public static final int NOT_FOUND = 100;


    public static final Dish dish1 = new Dish(DISH_ID, "Харчо", now(), valueOf(500), RESTAURANT_HARBIN);

    public static final Dish dish2 = new Dish(DISH_ID + 1, "Жаркое", now(), valueOf(500), RESTAURANT_HARBIN);
    public static final Dish dish3 = new Dish(DISH_ID + 2, "Компот", now(), valueOf(500), RESTAURANT_HARBIN);
    public static final Dish dish4 = new Dish(DISH_ID + 3, "Селедка", now(), valueOf(320), RESTAURANT_CI);
    public static final Dish dish5 = new Dish(DISH_ID + 4, "Пиво", now(), valueOf(150), RESTAURANT_CI);
    public static final Dish dish6 = new Dish(DISH_ID + 5, "Фрукты", of(2015, Month.APRIL, 16), valueOf(500), RESTAURANT_CI);
    public static final Dish dish7 = new Dish(DISH_ID + 6, "Солянка", of(2015, Month.APRIL, 30), valueOf(260), RESTAURANT_HARBIN);

    public static final List<Dish> dishes = Stream.of(dish3, dish2, dish1, dish4, dish5, dish6, dish7).
            sorted(Comparator.comparing(Dish::getDate).reversed().thenComparing((Dish::getName))).toList();

    static {
        RESTAURANT_HARBIN.setDishes(Set.of(dish1, dish2, dish3));
        RESTAURANT_HARBIN.setVotes(HARBIN_VOTES_NOW);
        RESTAURANT_CI.setDishes(Set.of(dish4, dish5));
        RESTAURANT_CI.setVotes(Set.of());
    }

    public static Dish getNewDish() {
        return new Dish(null, "Созданное блюдо", now(), valueOf(200));
    }

    public static Dish getUpdatedDish() {
        return new Dish(DISH_ID, "Обновленнoe харчо", dish1.getDate(), valueOf(500), RESTAURANT_HARBIN);
    }


    public static final Vote VOTE_USER_2_HARBIN_NOW = new Vote(VOTE_ID + 4, RESTAURANT_HARBIN, user_2, now());
    public static final Vote VOTE_USER_2_HANOY_NOW = new Vote(VOTE_ID + 4, RESTAURANT_HANOY, user_2, now());

    public static Restaurant getNew() {
        return new Restaurant(null, "Resturant_created");
    }

    public static Restaurant setUpdatedDish(Restaurant restaurant, Dish dish) {
        final Restaurant updated = new Restaurant(restaurant.getId(), restaurant.getName());
        updated.setDishes(Set.of(getUpdatedDish(), dish2, dish3));
        return updated;

    }

    public static Restaurant getUpdated() {
        final Restaurant new_Harbin = new Restaurant(RESTAURAUNT_HARBIN_ID, "Харбин обновлен");
        new_Harbin.setVotes(HARBIN_VOTES_NOW);
        return new_Harbin;
    }

}
