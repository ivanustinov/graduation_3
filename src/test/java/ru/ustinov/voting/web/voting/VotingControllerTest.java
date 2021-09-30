package ru.ustinov.voting.web.voting;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.repository.VoteRepository;
import ru.ustinov.voting.web.AbstractControllerTest;
import ru.ustinov.voting.web.dish.DishTestData;
import ru.ustinov.voting.web.restaurant.RestaurantTestData;
import ru.ustinov.voting.web.user.UserTestData;

import java.time.*;

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
    public static final LocalDate DATE = LocalDate.now();

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
                .andExpect(WITH_VOTES_DISHES_MATCHER.contentJson(RESTAURANT_HARBIN_TO, RESTAURANT_CI_TO));
    }


    @Test
    void voteAfterEleven() throws Exception {
        fixTime("T11:10:00Z");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurant_id", String.valueOf(RestaurantTestData.RESTAURAUNT_HARBIN_ID)))
                .andExpect(status().isCreated())
                .andDo(print());
        VOTE_MATCHER.assertMatch(voteRepository.getVoteByUserAndDate(user_2, DATE), VOTE_USER_2_HARBIN_NOW);
    }

    @Test
    void voteTwiceBeforeEleven() throws Exception {
        fixTime("T10:10:00Z");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurant_id", String.valueOf(RestaurantTestData.RESTAURAUNT_HARBIN_ID)))
                .andExpect(status().isCreated())
                .andDo(print());
        VOTE_MATCHER.assertMatch(voteRepository.getVoteByUserAndDate(user_2, DATE), VOTE_USER_2_HARBIN_NOW);
        perform(MockMvcRequestBuilders.put(REST_URL)
                .param("restaurant_id", String.valueOf(RestaurantTestData.RESTAURANT_HANOY.id())))
                .andExpect(status().isNoContent())
                .andDo(print());
        VOTE_MATCHER.assertMatch(voteRepository.getVoteByUserAndDate(user_2, DATE), VOTE_USER_2_HANOY_NOW);
    }


    @Test
    void voteTwiceAfterEleven() throws Exception {
        fixTime("T10:10:00Z");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurant_id", String.valueOf(RestaurantTestData.RESTAURAUNT_HARBIN_ID)))
                .andExpect(status().isCreated())
                .andDo(print());
        VOTE_MATCHER.assertMatch(voteRepository.getVoteByUserAndDate(user_2, DATE), VOTE_USER_2_HARBIN_NOW);
        fixTime("T11:10:00Z");
        perform(MockMvcRequestBuilders.put(REST_URL)
                .param("restaurant_id", String.valueOf(RestaurantTestData.RESTAURANT_HANOY.id())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
        VOTE_MATCHER.assertMatch(voteRepository.getVoteByUserAndDate(user_2, DATE), VOTE_USER_2_HARBIN_NOW);
    }


    private void fixTime(String time) {
        final Clock fixed = Clock.fixed(Instant.parse(DATE + time), ZoneId.of("Z"));
        final LocalDateTime dateTimeFixed = LocalDateTime.now(fixed);
        Mockito.mockStatic(LocalDateTime.class).when(() -> LocalDateTime.now()).thenReturn(dateTimeFixed);
    }


}