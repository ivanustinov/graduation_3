package ru.ustinov.voting.web.cash;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.service.RestaurantService;
import ru.ustinov.voting.to.NamedTo;
import ru.ustinov.voting.to.RestaurantTo;
import ru.ustinov.voting.web.AbstractControllerTest;
import ru.ustinov.voting.web.dish.DishController;
import ru.ustinov.voting.web.json.JsonUtil;
import ru.ustinov.voting.web.restaurant.RestaurantController;
import ru.ustinov.voting.web.restaurant.RestaurantTestData;
import ru.ustinov.voting.web.user.UserTestData;
import ru.ustinov.voting.web.voting.VotingController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.ustinov.voting.web.dish.DishTestData.*;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.*;
import static ru.ustinov.voting.web.voting.VoteTestData.HARBIN_VOTES_NOW;


/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 10.09.2021
 */

@WithUserDetails(value = UserTestData.ADMIN_MAIL)
public class SpringCacheTest extends AbstractControllerTest {


    static final String REST_URL_ADMIN = RestaurantController.REST_URL + '/';
    static final String REST_URL_USER = VotingController.REST_URL;

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    DishRepository dishRepository;


    @BeforeAll
    public static void initRestaurantTo() {
        setVotesAndDishes(RESTAURANT_HARBIN_TO, harbinDishesNow, HARBIN_VOTES_NOW.size());
        setVotesAndDishes(RESTAURANT_CI_TO, ciDishesNow, 0);
    }


    @Test
    public void cacheEvictWhenUpdateRestaurant() throws Exception {
        getRestaurantsWithDishes();
        final List<RestaurantTo> resBeforeUpdateUser = cacheManager.getCache("restaurants").get("getWithDishesToday", List.class);
        WITH_DISHES_MATCHER.assertMatch(resBeforeUpdateUser, Stream.of(RESTAURANT_CI_TO, RESTAURANT_HARBIN_TO).sorted(comparing(NamedTo::getName)).toList());
        final Restaurant updated = RestaurantTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL_ADMIN + RESTAURAUNT_HARBIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print());
        getRestaurantsWithDishes();
        final List<RestaurantTo> resAfteUpdateUser = cacheManager.getCache("restaurants").get("getWithDishesToday", List.class);
        WITH_DISHES_MATCHER.assertMatch(resAfteUpdateUser, Stream.of(RESTAURANT_CI_TO, getUpdatedTo()).sorted(comparing(NamedTo::getName)).toList());
    }


    private void getRestaurantsWithDishes() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_USER)
                ).andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    public void cacheEvictWhenUpdateDish() throws Exception {
        getRestaurantsWithDishes();
        final List<RestaurantTo> resBeforeUpdate = cacheManager.getCache("restaurants").get("getWithDishesToday", List.class);
        WITH_DISHES_MATCHER.assertMatch(resBeforeUpdate, Stream.of(RESTAURANT_CI_TO, RESTAURANT_HARBIN_TO).sorted(comparing(NamedTo::getName)).toList());
        System.out.println("Start Updating Dish");
        perform(MockMvcRequestBuilders.put(DishController.REST_URL + '/' + RESTAURAUNT_HARBIN_ID + '/'+ DISH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedDish())))
                .andDo(print());
        getRestaurantsWithDishes();
        final List<RestaurantTo> resAftereUpdate = cacheManager.getCache("restaurants").get("getWithDishesToday", List.class);
        WITH_DISHES_MATCHER.assertMatch(resAftereUpdate,
                Stream.of(RESTAURANT_CI_TO, setUpdatedDish(RESTAURANT_HARBIN, getUpdatedDish())).sorted(comparing(NamedTo::getName)).toList());
    }
}


