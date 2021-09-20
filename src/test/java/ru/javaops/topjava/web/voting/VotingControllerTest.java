package ru.javaops.topjava.web.voting;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaops.topjava.repository.VoteRepository;
import ru.javaops.topjava.web.AbstractControllerTest;

import java.time.LocalTime;
import java.util.List;

import static java.time.LocalDate.now;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaops.topjava.web.dish.DishTestData.ciDishesNow;
import static ru.javaops.topjava.web.dish.DishTestData.harbinDishesNow;
import static ru.javaops.topjava.web.restaurant.RestaurantTestData.*;
import static ru.javaops.topjava.web.user.UserTestData.USER_2_MAIL;
import static ru.javaops.topjava.web.user.UserTestData.user_2;
import static ru.javaops.topjava.web.voting.VoteTestData.*;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 06.09.2021
 */
@WithUserDetails(value = USER_2_MAIL)
class VotingControllerTest extends AbstractControllerTest {

    @BeforeAll
    public static void initRestaurantTo() {
        setVotesAndDishes(RESTAURANT_HARBIN_TO, harbinDishesNow, HARBIN_VOTES_NOW);
        setVotesAndDishes(RESTAURANT_CI_TO, ciDishesNow, List.of());
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
                .andExpect(WITH_DISHES_MATCHER.contentJson(RESTAURANT_CI_TO, RESTAURANT_HARBIN_TO));
    }

    @Test
    void getWithVotesAndDishesToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "results"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(WITH_VOTES_DISHES_MATCHER.contentJson(RESTAURANT_HARBIN_TO, RESTAURANT_CI_TO));
    }

    @Test
    void voteBeforeEleven() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurant_id", String.valueOf(RESTAURAUNT_HARBIN_ID))
                .param("time", String.valueOf(LocalTime.of(10, 0))))
                .andExpect(status().isNoContent())
                .andDo(print());
        VOTE_MATCHER.assertMatch(voteRepository.getVoteByUserAndDate(user_2, now()), VOTE_USER_2_HARBIN_NOW);
    }

    @Test
    void voteAfterEleven() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurant_id", String.valueOf(RESTAURAUNT_HARBIN_ID))
                .param("time", String.valueOf(LocalTime.of(12, 0))))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
        Assertions.assertNull(voteRepository.getVoteByUserAndDate(user_2, now()));
    }

//    @Test
//    @Commit
//    void voteTwice() throws Exception {
//        for (int i = 0; i < 2; i++) {
//            voteAnotherOne(RESTAURAUNT_HARBIN_ID, LocalTime.of(10, 0));
//        }
//        voteAnotherOne(RESTAURANT_HANOY.getId(),  LocalTime.of(10, 0));
//        List<Vote> votesByUserAndDate = voteRepository.getVotesByUserAndDate(user_2, now());
//        Assertions.assertEquals(1, votesByUserAndDate.size());
//        VOTE_MATCHER_WITH_USER.assertMatch(votesByUserAndDate.get(0), VOTE_USER_2_HANOY_NOW);
//    }

//    private void voteAnotherOne(int restaurant_id, LocalTime time) throws Exception {
//        perform(MockMvcRequestBuilders.post(REST_URL)
//                .param("restaurant_id", String.valueOf(restaurant_id))
//                .param("time", String.valueOf(time)))
//                .andExpect(status().isNoContent())
//                .andDo(print());
//    }
}