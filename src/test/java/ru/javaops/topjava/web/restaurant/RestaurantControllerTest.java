package ru.javaops.topjava.web.restaurant;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.repository.RestaurantRepository;
import ru.javaops.topjava.web.AbstractControllerTest;
import ru.javaops.topjava.web.json.JsonUtil;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaops.topjava.web.dish.DishTestData.ciDishesNow;
import static ru.javaops.topjava.web.dish.DishTestData.harbinDishesNow;
import static ru.javaops.topjava.web.restaurant.RestaurantTestData.*;
import static ru.javaops.topjava.web.user.UserTestData.*;
import static ru.javaops.topjava.web.voting.VoteTestData.HARBIN_VOTES_NOW;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 06.09.2021
 */
@WithUserDetails(value = ADMIN_MAIL)
class RestaurantControllerTest extends AbstractControllerTest {
    static final String REST_URL = RestaurantController.REST_URL + '/';


    @BeforeAll
    public static void initRestaurantTo() {
        setVotesAndDishes(RESTAURANT_HARBIN_TO, harbinDishesNow, HARBIN_VOTES_NOW);
        setVotesAndDishes(RESTAURANT_CI_TO, ciDishesNow, List.of());
    }

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURAUNT_HARBIN_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RestaurantTestData.RESTAURANT_MATCHER.contentJson(RESTAURANT_HARBIN));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURAUNT_HARBIN_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER.contentJson(RESTAURANTS));
    }

    @Test
    void getWithDishesByDate() throws Exception {
               perform(MockMvcRequestBuilders.get(REST_URL + "with-dishes-by-date")
                .param("date", String.valueOf(LocalDate.now())))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(WITH_DISHES_MATCHER.contentJson(RESTAURANT_CI_TO,
                        RESTAURANT_HARBIN_TO));
    }


    @Test
    void getWithVotesDishesAndUsersByDate() throws Exception{
                perform(MockMvcRequestBuilders.get(REST_URL + "with-votes-users-by-date")
                .param("date", String.valueOf(LocalDate.now())))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(WITH_VOTES_DISHES_USER_MATCHER.contentJson(RESTAURANT_HARBIN_TO, RESTAURANT_CI_TO));
    }

    @Test
    void delete() throws Exception{
        perform(MockMvcRequestBuilders.delete(REST_URL + RESTAURAUNT_HARBIN_ID))
                .andExpect(status().isNoContent());
        assertFalse(restaurantRepository.get(RESTAURAUNT_HARBIN_ID).isPresent());
    }

    @Test
    void update() throws Exception {
        final Restaurant updated = RestaurantTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + RESTAURAUNT_HARBIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent())
                .andDo(print());
        RestaurantTestData.RESTAURANT_MATCHER.assertMatch(restaurantRepository.getById(RESTAURAUNT_HARBIN_ID), updated);
    }

    @Test
    void createWithLocation() throws Exception {
        final Restaurant newRest = RestaurantTestData.getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRest)))
                .andDo(print());
        final Restaurant created = RestaurantTestData.RESTAURANT_MATCHER.readFromJson(action);
        final int id = created.id();
        newRest.setId(id);
        RestaurantTestData.RESTAURANT_MATCHER.assertMatch(created, newRest);
        RestaurantTestData.RESTAURANT_MATCHER.assertMatch(restaurantRepository.getById(id), newRest);
    }
}