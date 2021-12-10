package ru.ustinov.voting.web.restaurant;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.repository.RestaurantRepository;
import ru.ustinov.voting.repository.VoteRepository;
import ru.ustinov.voting.service.VoteService;
import ru.ustinov.voting.web.AbstractControllerTest;
import ru.ustinov.voting.web.formatter.DateFormatter;
import ru.ustinov.voting.web.json.JsonUtil;
import ru.ustinov.voting.web.user.UserTestData;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.ustinov.voting.service.RestaurantService.EXCEPTION_GETTING_RESULT_BEFORE_VOTING_TIME_LEFT;
import static ru.ustinov.voting.web.GlobalExceptionHandler.EXCEPTION_DUPLICATE_RESTAURANT_NAME;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.*;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 06.09.2021
 */
@WithUserDetails(value = UserTestData.ADMIN_MAIL)
class RestaurantRestControllerTest extends AbstractControllerTest {

    static final String REST_URL = RestaurantRestController.REST_URL + '/';

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private DishRepository dishRepository;

    @MockBean
    private VoteService voteService;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURAUNT_HARBIN_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RestaurantTestData.RESTAURANT_MATCHER.contentJson(RESTAURANT_HARBIN));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(jsonPath("$.message").value(messageSourceAccessor.getMessage("error.entityWithIdNotFound", new String[]{String.valueOf(NOT_FOUND)})));
    }

    @Test
    void getWithoutDish() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "without")
                .param("date", DateFormatter.format(LocalDate.now())))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
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
                .param("date", DateFormatter.format(LocalDate.now())))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(WITH_DISHES_MATCHER.contentJson(RESTAURANT_CI,
                        RESTAURANT_HARBIN));
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getWithDishesToday() throws Exception {
        perform(MockMvcRequestBuilders.get("/rest/profile/voting"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(WITH_DISHES_MATCHER.contentJson(RESTAURANT_CI, RESTAURANT_HARBIN));
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getResultAfterVotingTimeLeft() throws Exception {
        Mockito.when(voteService.getVotingTime()).thenReturn(LocalTime.now().minusMinutes(10));
        perform(MockMvcRequestBuilders.get("/rest/profile/result"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TO_MATCHER.contentJson(RESTAURANT_HARBIN_TO));
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getResultBeforeVotingTimeLeft() throws Exception {
        Mockito.when(voteService.getVotingTime()).thenReturn(LocalTime.now().plusMinutes(10));
        perform(MockMvcRequestBuilders.get("/rest/profile/result"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.message").value(messageSourceAccessor.getMessage(EXCEPTION_GETTING_RESULT_BEFORE_VOTING_TIME_LEFT)));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + RESTAURAUNT_HARBIN_ID))
                .andExpect(status().isNoContent());
        assertFalse(restaurantRepository.get(RESTAURAUNT_HARBIN_ID).isPresent());
        assertEquals(0, dishRepository.getDishesByRestaurant(RESTAURAUNT_HARBIN_ID).size());
        assertEquals(0, voteRepository.getVoteByRestaurant(RESTAURAUNT_HARBIN_ID).size());
    }

    @Test
    void update() throws Exception {
        final Restaurant updated = RestaurantTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + RESTAURAUNT_HARBIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent())
                .andDo(print());
        RESTAURANT_MATCHER.assertMatch(restaurantRepository.getById(RESTAURAUNT_HARBIN_ID), updated);
    }

    @Test
    void updateDuplicate() throws Exception {
        final Restaurant updated = new Restaurant(RESTAURAUNT_HARBIN_ID, "Си");
        perform(MockMvcRequestBuilders.put(REST_URL + RESTAURAUNT_HARBIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").
                        value(messageSourceAccessor.getMessage(EXCEPTION_DUPLICATE_RESTAURANT_NAME, new String[]{"Си"})))
                .andDo(print());
    }

    @Test
    void updateInvalid() throws Exception {
        final Restaurant updated = new Restaurant(RESTAURAUNT_HARBIN_ID, null);
        perform(MockMvcRequestBuilders.put(REST_URL + RESTAURAUNT_HARBIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSourceAccessor.getMessage("NotBlank", new String[]{"Название"})))
                .andDo(print());
    }

    @Test
    void updateNotIdConsistent() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(RESTAURANT_HARBIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSourceAccessor.getMessage("error.entityMustHaveId", new String[]{"Restaurant", String.valueOf(NOT_FOUND)})))
                .andDo(print());
    }

    @Test
    void createWithLocation() throws Exception {
        final Restaurant newRest = RestaurantTestData.getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRest)))
                .andDo(print());
        final Restaurant created = RESTAURANT_MATCHER.readFromJson(action);
        final int id = created.id();
        newRest.setId(id);
        RESTAURANT_MATCHER.assertMatch(created, newRest);
        RESTAURANT_MATCHER.assertMatch(restaurantRepository.getById(id), newRest);
    }


    @Test
    void createDuplicate() throws Exception {
        final Restaurant newRest = new Restaurant(null, "Харбин");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").
                        value(messageSourceAccessor.getMessage(EXCEPTION_DUPLICATE_RESTAURANT_NAME, new String[]{"Харбин"})))
                .andDo(print());
    }

    @Test
    void createInvalid() throws Exception {
        final Restaurant newRest = new Restaurant(null, null);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRest)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }
}