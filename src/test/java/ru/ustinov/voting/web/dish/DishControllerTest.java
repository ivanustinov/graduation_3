package ru.ustinov.voting.web.dish;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.web.AbstractControllerTest;
import ru.ustinov.voting.web.json.JsonUtil;
import ru.ustinov.voting.web.user.UserTestData;

import java.time.LocalDate;

import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.ustinov.voting.web.dish.DishTestData.*;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.RESTAURANT_HARBIN;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.RESTAURAUNT_HARBIN_ID;

@WithUserDetails(value = UserTestData.ADMIN_MAIL)
class DishControllerTest extends AbstractControllerTest {

    private static final String REST_URL = DishController.REST_URL + '/';

    @Autowired
    private DishRepository dishRepository;

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURAUNT_HARBIN_ID + "/" + DISH_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURAUNT_HARBIN_ID + "/" + DISH_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson(dish1));
    }

    @Test
    void getDishesByDateAndRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURAUNT_HARBIN_ID)
                .param("date", String.valueOf(now())))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson(harbinDishesNow));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURAUNT_HARBIN_ID + "/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + RESTAURAUNT_HARBIN_ID + "/" + DISH_ID))
                .andExpect(status().isNoContent());
        assertFalse(dishRepository.get(DISH_ID).isPresent());
    }

    @Test
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + RESTAURAUNT_HARBIN_ID + "/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void update() throws Exception {
        Dish updated = getUpdatedDish();
        perform(MockMvcRequestBuilders.put(REST_URL + RESTAURAUNT_HARBIN_ID + "/" + DISH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());
        updated.setRestaurant(RESTAURANT_HARBIN);
        MATCHER.assertMatch(dishRepository.getById(DISH_ID), updated);
    }

    @Test
    public void createWithLocation() throws Exception {
        Dish newDish = getNewDish();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + RESTAURAUNT_HARBIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDish)))
                .andDo(print());
        Dish created = MATCHER.readFromJson(action);
        int newId = created.id();
        newDish.setId(newId);
        MATCHER.assertMatch(created, newDish);
        MATCHER.assertMatch(dishRepository.getById(newId), newDish);
    }

    @Test
    void createInvalid() throws Exception {
        Dish invalid = new Dish(null, null, now(), valueOf(200));
        perform(MockMvcRequestBuilders.post(REST_URL+ RESTAURAUNT_HARBIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateInvalid() throws Exception {
        Dish invalid = new Dish(DISH_ID, null, now(), valueOf(200));
        perform(MockMvcRequestBuilders.put(REST_URL + RESTAURAUNT_HARBIN_ID + "/" + DISH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicate() {
        Dish invalid = new Dish(dish2.getId(), "Харчо", now(), valueOf(500));
        assertThrows(Exception.class, () ->
                perform(MockMvcRequestBuilders.put(REST_URL + RESTAURAUNT_HARBIN_ID + "/" + dish2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeValue(invalid)))
                        .andDo(print())
        );
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicate() {
        Dish invalid = new Dish(null, "Харчо", now(), valueOf(500));
        assertThrows(Exception.class, () ->
                perform(MockMvcRequestBuilders.post(REST_URL+ RESTAURAUNT_HARBIN_ID )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeValue(invalid)))
                        .andDo(print())
                        .andExpect(status().isUnprocessableEntity())
        );
    }
}