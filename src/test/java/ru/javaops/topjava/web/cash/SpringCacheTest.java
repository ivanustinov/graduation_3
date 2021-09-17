package ru.javaops.topjava.web.cash;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaops.topjava.model.NamedEntity;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.repository.DishRepository;
import ru.javaops.topjava.repository.RestaurantRepository;
import ru.javaops.topjava.web.AbstractControllerTest;
import ru.javaops.topjava.web.dish.DishController;
import ru.javaops.topjava.web.json.JsonUtil;
import ru.javaops.topjava.web.restaurant.RestaurantController;
import ru.javaops.topjava.web.restaurant.RestaurantTestData;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static ru.javaops.topjava.web.dish.DishTestData.DISH_ID;
import static ru.javaops.topjava.web.dish.DishTestData.getUpdatedDish;
import static ru.javaops.topjava.web.restaurant.RestaurantTestData.*;
import static ru.javaops.topjava.web.user.UserTestData.ADMIN_MAIL;


/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 10.09.2021
 */
@WithUserDetails(value = ADMIN_MAIL)
public class SpringCacheTest extends AbstractControllerTest {

    static final String REST_URL = RestaurantController.REST_URL + '/';

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    DishRepository dishRepository;

    @Test
    public void cacheEvictWhenUpdateRestaurant() throws Exception {
        final List<Restaurant> restaurantsCashBeforeUpdate = restaurantRepository.getWithDishesByDate(LocalDate.now()).get();
        WITH_DISHES_MATCHER.assertMatch(restaurantsCashBeforeUpdate, Stream.of(RESTAURANT_CI, RESTAURANT_HARBIN).sorted(comparing(NamedEntity::getName)).toList());
        System.out.println("Start Updating");
        final Restaurant updated = RestaurantTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + RESTAURAUNT_HARBIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print());
        final List<Restaurant> restaurantsCacheWithDishesAfterUpdate = restaurantRepository.getWithDishesByDate(LocalDate.now()).get();
        WITH_DISHES_MATCHER.assertMatch(restaurantsCacheWithDishesAfterUpdate, Stream.of(RESTAURANT_CI, getUpdated()).sorted(comparing(NamedEntity::getName)).toList());
    }


    @Test
    public void cacheEvictWhenUpdateDish() throws Exception {
        final List<Restaurant> restaurantsCashBeforeUpdate = restaurantRepository.getWithDishesByDate(LocalDate.now()).get();
        WITH_DISHES_MATCHER.assertMatch(restaurantsCashBeforeUpdate, Stream.of(RESTAURANT_CI, RESTAURANT_HARBIN).sorted(comparing(NamedEntity::getName)).toList());
        System.out.println("Start Updating Dish");
        perform(MockMvcRequestBuilders.put(DishController.REST_URL + '/' + DISH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedDish()))
                .param("restaurant_id", String.valueOf(RESTAURAUNT_HARBIN_ID)))
                .andDo(print());
        final List<Restaurant> restaurantsCacheWithDishesAfterUpdate = restaurantRepository.getWithDishesByDate(LocalDate.now()).get();
        WITH_DISHES_MATCHER.assertMatch(restaurantsCacheWithDishesAfterUpdate,
                Stream.of(RESTAURANT_CI, setUpdatedDish(RESTAURANT_HARBIN, getUpdatedDish())).sorted(comparing(NamedEntity::getName)).toList());
    }
}


