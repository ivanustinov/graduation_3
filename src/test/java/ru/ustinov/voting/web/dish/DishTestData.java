package ru.ustinov.voting.web.dish;

import ru.ustinov.voting.MatcherFactory;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.web.json.JsonUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.now;
import static java.time.LocalDate.of;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.RESTAURANT_CI;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.RESTAURANT_HARBIN;

public class DishTestData {

    public static final MatcherFactory.Matcher<Dish> MATCHER = MatcherFactory
            .usingIgnoringFieldsComparator(Dish.class, "restaurant.name", "restaurant.dishes");

    public static final MatcherFactory.Matcher<Dish> MATCHER_WITHOUT_ID_RESTAURANT = MatcherFactory
            .usingIgnoringFieldsComparator(Dish.class, "id", "restaurant");

    public static final int DISH_ID = 1;

    public static final int NOT_FOUND = 100;

    final static LocalDate now = now();

    public static BigDecimal setScale(BigDecimal price) {
        return price.setScale(2, RoundingMode.DOWN);
    }

    public static final Dish dish1 = new Dish(DISH_ID, "Харчо", now, setScale(valueOf(300)), RESTAURANT_HARBIN);
    public static final Dish dish2 = new Dish(DISH_ID + 1, "Жаркое", now, setScale(valueOf(356.45)), RESTAURANT_HARBIN);
    public static final Dish dish3 = new Dish(DISH_ID + 2, "Компот", now, setScale(valueOf(30)), RESTAURANT_HARBIN);
    public static final Dish dish4 = new Dish(DISH_ID + 3, "Сельдь под шубой", now, valueOf(176.47), RESTAURANT_CI);

    public static final Dish dish5 = new Dish(DISH_ID + 4, "Пиво", now, setScale(valueOf(150)), RESTAURANT_CI);
    public static final Dish dish6 = new Dish(null, "Жаркое", now, setScale(valueOf(260)), RESTAURANT_HARBIN);
    public static final Dish dish7 = new Dish(null, "Пюре картофельное с голубцами", now, setScale(valueOf(225.78)), RESTAURANT_HARBIN);
    public static final Dish dish8 = new Dish(null, "Кофе с круассаном", now, setScale(valueOf(100.35)), RESTAURANT_HARBIN);

    public static List<Dish> harbinDishesNow = Stream.of(dish2, dish3, dish1).sorted(Comparator.comparing(Dish::getName)).toList();
    public static List<Dish> harbinDishesInThePastWithDuplicate = List.of(dish6, dish7, dish8);

    public static List<Dish> harbinNewDishesFromLastMenu = List.copyOf(harbinDishesNow);

    public static final List<Dish> newHarbinDishes;

    static {
        newHarbinDishes = harbinNewDishesFromLastMenu.stream().map(dish -> {
            Dish newDish = new Dish(dish);
            newDish.setId(null);
            newDish.setDate(now.plusDays(1));
            return newDish;
        }).toList();
    }

    public static List<Dish> ciDishesNow = Stream.of(dish4, dish5).sorted(Comparator.comparing(Dish::getName)).toList();

    public static Dish getNewDish() {
        return new Dish(null, "Созданное блюдо", now, setScale(valueOf(200.34)), RESTAURANT_HARBIN);
    }

    public static Dish getUpdatedDish() {
        return new Dish(DISH_ID, "Обновленнoe харчо", now, setScale(valueOf(500)), RESTAURANT_HARBIN);
    }
}
