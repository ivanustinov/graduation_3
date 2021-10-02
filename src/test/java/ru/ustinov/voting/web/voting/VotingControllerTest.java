package ru.ustinov.voting.web.voting;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ustinov.voting.repository.VoteRepository;
import ru.ustinov.voting.web.AbstractControllerTest;
import ru.ustinov.voting.web.json.JsonUtil;
import ru.ustinov.voting.web.user.UserTestData;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

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

    @Autowired
    private EntityManager entityManager;

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
                .andExpect(WITH_DISHES_MATCHER.contentJson(RESTAURANT_CI, RESTAURANT_HARBIN));
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
        final MockedStatic<LocalTime> localTimeMockedStatic = fixTime("T11:10:00Z");
        try (localTimeMockedStatic) {
            perform(MockMvcRequestBuilders.post(REST_URL)
                    .param("restaurant_id", String.valueOf(RESTAURAUNT_HARBIN_ID))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.writeValue(voteUser_2HarbinNow)))
                    .andDo(print());
        }
        entityManager.clear();
        VOTE_MATCHER.assertMatch(voteRepository.getVoteByUserAndDate(user_2, DATE), VOTE_USER_2_HARBIN_NOW);
    }

    @Test
    void voteTwiceBeforeEleven() throws Exception {
        final MockedStatic<LocalTime> localTimeMockedStatic = fixTime("T10:10:00Z");
        try (localTimeMockedStatic) {
            perform(MockMvcRequestBuilders.post(REST_URL)
                    .param("restaurant_id", String.valueOf(RESTAURAUNT_HARBIN_ID))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.writeValue(voteUser_2HarbinNow)))
                    .andDo(print());
        }
        entityManager.clear();
        VOTE_MATCHER.assertMatch(voteRepository.getVoteByUserAndDate(user_2, DATE), VOTE_USER_2_HARBIN_NOW);
        final MockedStatic<LocalTime> localTimeMockedStaticAnother = fixTime("T10:15:00Z");
        try (localTimeMockedStaticAnother) {
            perform(MockMvcRequestBuilders.put(REST_URL)
                    .param("restaurant_id", String.valueOf(RESTAURANT_HANOY.id())))
                    .andExpect(status().isNoContent())
                    .andDo(print());
        }
        entityManager.flush();
        entityManager.clear();
        VOTE_MATCHER.assertMatch(voteRepository.getVoteByUserAndDate(user_2, DATE), VOTE_USER_2_HANOY_NOW);
    }

    @Test
    void voteTwiceAfterEleven() throws Exception {
        final MockedStatic<LocalTime> localTimeMockedStatic = fixTime("T10:10:00Z");
        try (localTimeMockedStatic) {
            perform(MockMvcRequestBuilders.post(REST_URL)
                    .param("restaurant_id", String.valueOf(RESTAURAUNT_HARBIN_ID))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.writeValue(voteUser_2HarbinNow)))
                    .andDo(print());
        }
        entityManager.clear();
        VOTE_MATCHER.assertMatch(voteRepository.getVoteByUserAndDate(user_2, DATE), VOTE_USER_2_HARBIN_NOW);
        final MockedStatic<LocalTime> localTimeMockedStatic1 = fixTime("T11:10:00Z");
        try (localTimeMockedStatic1) {
            perform(MockMvcRequestBuilders.put(REST_URL)
                    .param("restaurant_id", String.valueOf(RESTAURANT_HANOY.id())))
                    .andExpect(status().isUnprocessableEntity())
                    .andDo(print());
        }
    }

    private MockedStatic<LocalTime> fixTime(String time) {
        final Instant instant = Instant.parse(DATE + time);
        final LocalTime timeFixed = LocalTime.ofInstant(instant, ZoneId.of("UTC"));
        final MockedStatic<LocalTime> localTimeMockedStatic = Mockito.mockStatic(LocalTime.class);
        localTimeMockedStatic.when(() -> LocalTime.now()).thenReturn(timeFixed);
        return localTimeMockedStatic;
    }

}