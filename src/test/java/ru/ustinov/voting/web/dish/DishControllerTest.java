package ru.ustinov.voting.web.dish;


import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.web.AbstractControllerTest;
import ru.ustinov.voting.web.GlobalExceptionHandler;
import ru.ustinov.voting.web.formatter.DateFormatter;
import ru.ustinov.voting.web.json.JsonUtil;

import java.time.LocalDate;

import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.ustinov.voting.web.dish.DishTestData.*;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.*;
import static ru.ustinov.voting.web.user.UserTestData.ADMIN_MAIL;
import static ru.ustinov.voting.web.user.UserTestData.USER_MAIL;

@WithUserDetails(ADMIN_MAIL)
class DishControllerTest extends AbstractControllerTest {

    private static final String REST_URL = "/rest/admin/dishes/";

    @Autowired
    private DishRepository dishRepository;

    @Test
    @WithUserDetails(USER_MAIL)
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
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURAUNT_HARBIN_ID + "/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getDishesByDateAndRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURAUNT_HARBIN_ID)
                .param("date", DateFormatter.format(LocalDate.now())))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson(harbinDishesNow));
    }

    @Test
    void getLastMenu() throws Exception {
        final LocalDate localDate = now().plusDays(1);
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURAUNT_HARBIN_ID + "/lastMenu")
                .param("date", DateFormatter.format(localDate)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson((harbinDishesNow)));
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
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(
                        messageSourceAccessor.getMessage("error.validationError")));
    }

    @Test
    void deletAllByRestaurantAndDate() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + RESTAURAUNT_HARBIN_ID)
                .param("date", DateFormatter.format(LocalDate.now())))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertTrue(dishRepository.getDishesByDateAndRestaurant(RESTAURAUNT_HARBIN_ID, LocalDate.now()).isEmpty());
    }

    @Test
    void update() throws Exception {
        Dish updated = getUpdatedDish();
        perform(MockMvcRequestBuilders.put(REST_URL + RESTAURAUNT_HARBIN_ID + "/" + DISH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());
        final Dish byId = dishRepository.getById(DISH_ID);
        final Object unproxy = Hibernate.unproxy(byId.getRestaurant());
        byId.setRestaurant((Restaurant) unproxy);
        MATCHER.assertMatch(byId, updated);
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
    }

    @Test
    public void createWithUnexistingRestaurant() throws Exception {
        Dish newDish = getNewDish();
        perform(MockMvcRequestBuilders.post(REST_URL + NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDish)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(
                        messageSourceAccessor.getMessage("restaurant.unexisting", new Object[]{NOT_FOUND})))
                .andDo(print());
    }

    @Test
    public void createDishesFromLastMenu() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + RESTAURAUNT_HARBIN_ID + "/lastMenu")
                .content(JsonUtil.writeValue(newHarbinDishesFromLastMenu))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
        MATCHER_WITHOUT_ID_RESTAURANT.assertMatch(
                dishRepository.getDishesByDateAndRestaurant(RESTAURAUNT_HARBIN_ID, now().plusDays(1)), newHarbinDishesFromLastMenu);
    }

    @Test
    public void createDishesFromLastMenuWithDuplicate() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + RESTAURAUNT_HARBIN_ID + "/lastMenu")
                .content(JsonUtil.writeValue(harbinDishesInThePastWithDuplicate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(
                        messageSourceAccessor.getMessage(GlobalExceptionHandler.EXCEPTION_DUPLICATE_DISH_NAME, new String[]{"Жаркое"})))
                .andDo(print());
        MATCHER_WITHOUT_ID_RESTAURANT.assertMatch(
                dishRepository.getDishesByDateAndRestaurant(RESTAURAUNT_HARBIN_ID, now()), harbinDishesNow);
    }

    @Test
    void createInvalid() throws Exception {
        Dish invalid = new Dish(null, null, now(), valueOf(200));
        perform(MockMvcRequestBuilders.post(REST_URL + RESTAURAUNT_HARBIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createDishInThePast() throws Exception {
        Dish invalid = new Dish(null, "newDish", now().minusDays(1), valueOf(200));
        perform(MockMvcRequestBuilders.post(REST_URL + RESTAURAUNT_HARBIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(messageSourceAccessor.getMessage("dish.create_in_the_past")));
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
    void updateDuplicate() throws Exception {
        Dish invalid = new Dish(dish2.getId(), "Харчо", now(), valueOf(500));
        perform(MockMvcRequestBuilders.put(REST_URL + RESTAURAUNT_HARBIN_ID + "/" + dish2.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message")
                        .value(messageSourceAccessor.getMessage(GlobalExceptionHandler.EXCEPTION_DUPLICATE_DISH_NAME, new String[]{"Харчо"})));
    }

    @Test
    void createDuplicate() throws Exception {
        Dish invalid = new Dish(null, "Харчо", now(), valueOf(500));
        perform(MockMvcRequestBuilders.post(REST_URL + RESTAURAUNT_HARBIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSourceAccessor.getMessage(GlobalExceptionHandler.EXCEPTION_DUPLICATE_DISH_NAME, new String[]{"Харчо"})));
    }

}