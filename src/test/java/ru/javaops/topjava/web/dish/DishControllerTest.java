package ru.javaops.topjava.web.dish;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.topjava.model.Dish;
import ru.javaops.topjava.repository.DishRepository;
import ru.javaops.topjava.web.AbstractControllerTest;
import ru.javaops.topjava.web.json.JsonUtil;

import java.time.LocalDate;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaops.topjava.web.dish.DishTestData.*;
import static ru.javaops.topjava.web.restaurant.RestaurantTestData.*;
import static ru.javaops.topjava.web.user.UserTestData.ADMIN_MAIL;
import static ru.javaops.topjava.web.user.UserTestData.USER_MAIL;

@WithUserDetails(value = ADMIN_MAIL)
class DishControllerTest extends AbstractControllerTest {

    private static final String REST_URL = DishController.REST_URL + '/';

    @Autowired
    private DishRepository dishRepository;

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DISH_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DISH_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson(dish1));
    }


    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson(dishes));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DISH_ID))
                .andExpect(status().isNoContent());
        assertFalse(dishRepository.get(DISH_ID).isPresent());
    }

    @Test
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void update() throws Exception {
        Dish updated = getUpdatedDish();
        perform(MockMvcRequestBuilders.put(REST_URL + DISH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated))
                .param("restaurant_id", String.valueOf(RESTAURAUNT_HARBIN_ID)))
                .andDo(print())
                .andExpect(status().isNoContent());
        updated.setRestaurant(RESTAURANT_HARBIN);
        MATCHER.assertMatch(dishRepository.getById(DISH_ID), updated);
    }

    @Test
    public void createWithLocation() throws Exception {
        Dish newDish = getNewDish();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDish))
                .param("restaurant_id", String.valueOf(RESTAURAUNT_HARBIN_ID)))
                .andDo(print());
        Dish created = MATCHER.readFromJson(action);
        int newId = created.id();
        newDish.setId(newId);
        newDish.setRestaurant(RESTAURANT_HARBIN);
        MATCHER.assertMatch(created, newDish);
        MATCHER.assertMatch(dishRepository.getById(newId), newDish);
    }


    @Test
    void createInvalid() throws Exception {
        Dish invalid = new Dish(null, null, LocalDate.now(), valueOf(200));
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .param("restaurant_id", String.valueOf(RESTAURAUNT_HARBIN_ID)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateInvalid() throws Exception {
        Dish invalid = new Dish(DISH_ID, null, LocalDate.now(), valueOf(200));
        perform(MockMvcRequestBuilders.put(REST_URL + DISH_ID )
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .param("restaurant_id", String.valueOf(RESTAURAUNT_HARBIN_ID)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }



    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicate() {
        Dish invalid = new Dish(dish2.getId(), "Харчо", LocalDate.now(), valueOf(500));
        assertThrows(Exception.class, () ->
                perform(MockMvcRequestBuilders.put(REST_URL + dish2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeValue(invalid))
                        .param("restaurant_id", String.valueOf(RESTAURAUNT_HARBIN_ID)))
                        .andDo(print())
        );
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicate() {
        Dish invalid = new Dish(null, "Харчо", LocalDate.now(), valueOf(500));
        assertThrows(Exception.class, () ->
                perform(MockMvcRequestBuilders.post(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeValue(invalid))
                        .param("restaurant_id", String.valueOf(RESTAURAUNT_HARBIN_ID)))
                        .andDo(print())
                        .andExpect(status().isUnprocessableEntity())
        );
    }
}