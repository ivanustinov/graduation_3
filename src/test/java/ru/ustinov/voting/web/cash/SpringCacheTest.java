package ru.ustinov.voting.web.cash;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ustinov.voting.model.NamedEntity;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.service.RestaurantService;
import ru.ustinov.voting.web.AbstractControllerTest;
import ru.ustinov.voting.web.json.JsonUtil;
import ru.ustinov.voting.web.restaurant.RestaurantRestController;
import ru.ustinov.voting.web.restaurant.RestaurantTestData;
import ru.ustinov.voting.web.user.UserTestData;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.*;


/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 10.09.2021
 */

@WithUserDetails(value = UserTestData.ADMIN_MAIL)
@ActiveProfiles(profiles = {"cache"})
public class SpringCacheTest extends AbstractControllerTest {

    static final String REST_URL_ADMIN = RestaurantRestController.REST_URL + '/';
    static final String REST_URL_USER = "/rest/profile/voting";

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    DishRepository dishRepository;

    @Autowired
    protected CacheManager cacheManager;

    @AfterEach
    void clearCache() {
        Objects.requireNonNull(cacheManager.getCache("result")).clear();
        Objects.requireNonNull(cacheManager.getCache("users")).clear();
        Objects.requireNonNull(cacheManager.getCache("restaurants")).clear();
    }

    @Test
    public void cacheEvictWhenUpdateRestaurant() throws Exception {
        getRestaurantsWithDishes();
        final SimpleKey key = new SimpleKey(new Object[]{LocalDate.now(), true});
        final List<Restaurant> resBeforeUpdate = cacheManager.getCache("restaurants").get(key, List.class);
        WITH_DISHES_MATCHER.assertMatch(resBeforeUpdate, Stream.of(RESTAURANT_CI, RESTAURANT_HARBIN).sorted(comparing(NamedEntity::getName)).toList());
        final Restaurant updated = RestaurantTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL_ADMIN + RESTAURAUNT_HARBIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print());
        getRestaurantsWithDishes();
        final List<Restaurant> resAfterUpdate = cacheManager.getCache("restaurants").get(key, List.class);
        WITH_DISHES_MATCHER.assertMatch(resAfterUpdate, Stream.of(RESTAURANT_CI, getUpdated()).sorted(comparing(NamedEntity::getName)).toList());
    }

    private void getRestaurantsWithDishes() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_USER)).andExpect(status().isOk()).andDo(print());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    public void cacheEvictWhenUpdateDish() throws Exception {
        final SimpleKey key = new SimpleKey(new Object[]{LocalDate.now(), true});
        getRestaurantsWithDishes();
        final List<Restaurant> resBeforeUpdate = cacheManager.getCache("restaurants").get(key, List.class);
        WITH_DISHES_MATCHER.assertMatch(resBeforeUpdate, Stream.of(RESTAURANT_CI, RESTAURANT_HARBIN).sorted(comparing(NamedEntity::getName)).toList());
        System.out.println("Start Updating Dish");
        perform(MockMvcRequestBuilders.put("/rest/admin/dishes" + '/' + RESTAURAUNT_HARBIN_ID + '/' + DISH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedDish())))
                .andDo(print());
        getRestaurantsWithDishes();
        final List<Restaurant> resAftereUpdate = cacheManager.getCache("restaurants").get(key, List.class);
        WITH_DISHES_MATCHER.assertMatch(resAftereUpdate,
                Stream.of(RESTAURANT_CI, setUpdatedDish(RESTAURANT_HARBIN, getUpdatedDish())).sorted(comparing(NamedEntity::getName)).toList());
    }
}


