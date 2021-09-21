package ru.javaops.topjava.web.cash;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.repository.DishRepository;
import ru.javaops.topjava.service.RestaurantService;
import ru.javaops.topjava.to.NamedTo;
import ru.javaops.topjava.to.RestaurantTo;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaops.topjava.web.dish.DishTestData.*;
import static ru.javaops.topjava.web.restaurant.RestaurantTestData.*;
import static ru.javaops.topjava.web.user.UserTestData.ADMIN_MAIL;
import static ru.javaops.topjava.web.voting.VoteTestData.HARBIN_VOTES_NOW;


/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 10.09.2021
 */
@WithUserDetails(value = ADMIN_MAIL)
public class SpringCacheTest extends AbstractControllerTest {


    static final String REST_URL = RestaurantController.REST_URL + '/';

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    DishRepository dishRepository;


    @BeforeAll
    public static void initRestaurantTo() {
        setVotesAndDishes(RESTAURANT_HARBIN_TO, harbinDishesNow, HARBIN_VOTES_NOW);
        setVotesAndDishes(RESTAURANT_CI_TO, ciDishesNow, List.of());
    }


    @Test
    public void cacheEvictWhenUpdateRestaurant() throws Exception {
        getRestaurantsWithDishes();
        final List<RestaurantTo> resBeforeUpdate = cacheManager.getCache("res").get(LocalDate.now(), List.class);
        WITH_DISHES_MATCHER.assertMatch(resBeforeUpdate, Stream.of(RESTAURANT_CI_TO, RESTAURANT_HARBIN_TO).sorted(comparing(NamedTo::getName)).toList());
        final Restaurant updated = RestaurantTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + RESTAURAUNT_HARBIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print());
        getRestaurantsWithDishes();
        final List<RestaurantTo> resAfteUpdate = cacheManager.getCache("res").get(LocalDate.now(), List.class);
        WITH_DISHES_MATCHER.assertMatch(resAfteUpdate, Stream.of(RESTAURANT_CI_TO, getUpdatedTo()).sorted(comparing(NamedTo::getName)).toList());
    }

    private void getRestaurantsWithDishes() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "with-dishes-by-date")
                .param("date", String.valueOf(LocalDate.now())))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    public void cacheEvictWhenUpdateDish() throws Exception {
        getRestaurantsWithDishes();
        final List<RestaurantTo> resBeforeUpdate = cacheManager.getCache("res").get(LocalDate.now(), List.class);
        WITH_DISHES_MATCHER.assertMatch(resBeforeUpdate, Stream.of(RESTAURANT_CI_TO, RESTAURANT_HARBIN_TO).sorted(comparing(NamedTo::getName)).toList());
        System.out.println("Start Updating Dish");
        perform(MockMvcRequestBuilders.put(DishController.REST_URL + '/' + DISH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedDish()))
                .param("restaurant_id", String.valueOf(RESTAURAUNT_HARBIN_ID)))
                .andDo(print());
        getRestaurantsWithDishes();
        final List<RestaurantTo> resAftereUpdate = cacheManager.getCache("res").get(LocalDate.now(), List.class);
        WITH_DISHES_MATCHER.assertMatch(resAftereUpdate,
                Stream.of(RESTAURANT_CI_TO, setUpdatedDish(RESTAURANT_HARBIN, getUpdatedDish())).sorted(comparing(NamedTo::getName)).toList());
    }
}


