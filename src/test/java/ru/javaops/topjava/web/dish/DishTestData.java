package ru.javaops.topjava.web.dish;

import ru.javaops.topjava.MatcherFactory;
import ru.javaops.topjava.model.Dish;

import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.now;
import static java.time.LocalDate.of;
import static ru.javaops.topjava.web.restaurant.RestaurantTestData.RESTAURANT_CI;
import static ru.javaops.topjava.web.restaurant.RestaurantTestData.RESTAURANT_HARBIN;

public class DishTestData {
    public static final MatcherFactory.Matcher<Dish> MATCHER = MatcherFactory
            .usingIgnoringFieldsComparator(Dish.class, "");

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

    public static List<Dish> harbinDishesNow = Stream.of(dish2, dish3, dish1).sorted(Comparator.comparing(Dish::getName)).toList();
    public static List<Dish> ciDishesNow = Stream.of(dish4, dish5).sorted(Comparator.comparing(Dish::getName)).toList();

    public static Dish getNewDish() {
        return new Dish(null, "Созданное блюдо", now(), valueOf(200));
    }

    public static Dish getUpdatedDish() {
        return new Dish(DISH_ID, "Обновленнoe харчо", dish1.getDate(), valueOf(500));
    }
}
