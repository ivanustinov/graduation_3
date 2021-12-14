package ru.ustinov.voting.web.menu;

import ru.ustinov.voting.MatcherFactory;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.to.MenusTo;

import java.time.LocalDate;
import java.util.List;

import static ru.ustinov.voting.web.restaurant.RestaurantTestData.*;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 17.11.2021
 */
public class MenusTestData {

    public static final MatcherFactory.Matcher<MenusTo> MENUS_TO_MATCHER = MatcherFactory.usingRecurciveIgnoringFieldsComparator(MenusTo.class, "restaurants.dishes");

    public static final MenusTo MENUS_TO_2015_04_16 = new MenusTo(List.of(RESTAURANT_CI, RESTAURANT_HANOY, RESTAURANT_HARBIN), LocalDate.of(2015, 4, 16));
    public static final MenusTo MENUS_TO_NOW = new MenusTo(List.of(RESTAURANT_CI, RESTAURANT_HARBIN), LocalDate.now());

    public static final MenusTo MENUS_TO_COPIED = new MenusTo(List.of(RESTAURANT_CI, RESTAURANT_HARBIN), LocalDate.now().plusDays(1));
}
