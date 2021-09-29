package ru.ustinov.voting.web.voting;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ustinov.voting.repository.VoteRepository;
import ru.ustinov.voting.web.AbstractControllerTest;
import ru.ustinov.voting.web.dish.DishTestData;
import ru.ustinov.voting.web.restaurant.RestaurantTestData;
import ru.ustinov.voting.web.user.UserTestData;

import java.time.LocalTime;
import java.util.List;

import static java.time.LocalDate.now;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.*;
import static ru.ustinov.voting.web.user.UserTestData.user_2;
import static ru.ustinov.voting.web.voting.VoteTestData.*;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 06.09.2021
 */
@WithUserDetails(value = UserTestData.USER_2_MAIL)
class VotingControllerTest extends AbstractControllerTest {

    @BeforeAll
    public static void initRestaurantTo() {
        RestaurantTestData.setVotesAndDishes(RESTAURANT_HARBIN_TO, DishTestData.harbinDishesNow, HARBIN_VOTES_NOW.size());
        RestaurantTestData.setVotesAndDishes(RESTAURANT_CI_TO, DishTestData.ciDishesNow, 0);
    }

    public static final String REST_URL = VotingController.REST_URL + '/';

    @Autowired
    private VoteRepository voteRepository;

    @Test
    void getWithDishesToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RestaurantTestData.WITH_DISHES_MATCHER.contentJson(RESTAURANT_CI_TO, RESTAURANT_HARBIN_TO));
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getMyVoteToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "my-vote"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_MATCHER.contentJson(voteUserHarbinNow));
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getMyVotes() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "my-votes"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_MATCHER.contentJson(USER_VOTES));
    }

    @Test
    void getWithVotesAndDishesToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "results"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RestaurantTestData.WITH_VOTES_DISHES_MATCHER.contentJson(RESTAURANT_HARBIN_TO, RESTAURANT_CI_TO));
    }


    @Test
    void voteAfterEleven() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurant_id", String.valueOf(RestaurantTestData.RESTAURAUNT_HARBIN_ID))
                .param("time", String.valueOf(LocalTime.of(12, 0))))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
        Assertions.assertNull(voteRepository.getVoteByUserAndDate(user_2, now()));
    }

    @Test
    void voteAnotherOne() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurant_id", String.valueOf(RestaurantTestData.RESTAURAUNT_HARBIN_ID))
                .param("time", String.valueOf(LocalTime.of(10, 0))))
                .andExpect(status().isCreated())
                .andDo(print());
        VOTE_MATCHER.assertMatch(voteRepository.getVoteByUserAndDate(user_2, now()), VOTE_USER_2_HARBIN_NOW);
        perform(MockMvcRequestBuilders.put(REST_URL)
                .param("restaurant_id", String.valueOf(RestaurantTestData.RESTAURANT_HANOY.id()))
                .param("time", String.valueOf(LocalTime.of(10, 30))))
                .andExpect(status().isNoContent())
                .andDo(print());
        VOTE_MATCHER.assertMatch(voteRepository.getVoteByUserAndDate(user_2, now()), VOTE_USER_2_HANOY_NOW);
    }
}