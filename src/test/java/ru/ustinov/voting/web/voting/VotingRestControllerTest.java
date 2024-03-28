package ru.ustinov.voting.web.voting;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.repository.VoteRepository;
import ru.ustinov.voting.service.VoteService;
import ru.ustinov.voting.web.AbstractControllerTest;
import ru.ustinov.voting.web.json.JsonUtil;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.ustinov.voting.service.VoteService.EXCEPTION_VOTING_AFTER_VOTING_TIME_IS_UP;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.NOT_FOUND;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.*;
import static ru.ustinov.voting.web.user.UserTestData.*;
import static ru.ustinov.voting.web.voting.VoteTestData.*;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 06.09.2021
 */

@WithUserDetails(USER_MAIL)
class VotingRestControllerTest extends AbstractControllerTest {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoteService voteService;

    public static final String REST_URL = VotingRestController.REST_URL + '/';

    public static final LocalDate DATE = LocalDate.now();

    public static final LocalTime TIME = LocalTime.of(11, 0);

    @Test
    void getMyVoteToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_MATCHER.contentJson(voteUserHarbinNow));
    }

    @Test
    void getMyVotes() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "my-votes"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_MATCHER.contentJson(USER_VOTES));
    }

    @Test
    void voteAfterVoteTimeEnds() throws Exception {
        final LocalTime votingTime = voteService.getVotingTime();
        final MockedStatic<LocalTime> localTimeMockedStatic = fixCurrentTime(votingTime.plusMinutes(10));
        try (localTimeMockedStatic) {
            perform(MockMvcRequestBuilders.post(REST_URL)
                    .param("restaurant_id", String.valueOf(RESTAURAUNT_HARBIN_ID)))
                    .andExpect(status().isConflict())
                    .andDo(print())
                    .andExpect(jsonPath("$.message").value(messageSourceAccessor.getMessage(EXCEPTION_VOTING_AFTER_VOTING_TIME_IS_UP)));
        }
    }

    @Test
    void voteForUnexistingRestaurant() throws Exception {
        final LocalTime votingTime = voteService.getVotingTime();
        final MockedStatic<LocalTime> localTimeMockedStatic = fixCurrentTime(votingTime.minusMinutes(10));
        try (localTimeMockedStatic) {
            perform(MockMvcRequestBuilders.post(REST_URL)
                    .param("restaurant_id", String.valueOf(NOT_FOUND)))
                    .andExpect(status().isUnprocessableEntity())
                    .andDo(print());
        }
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void setVotingTime() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "/set_time")
                .param("time", String.valueOf(TIME)))
                .andDo(print());
        assertEquals(TIME, voteService.getVotingTime());
    }

    @Test
    void getVotingTime() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/voting_time"))
                .andExpect(result -> assertThat(voteService.getVotingTime())
                        .isEqualTo(JsonUtil.readValue(result.getResponse().getContentAsString(), LocalTime.class)))
                .andDo(print());
    }

//    @Test
//    @WithUserDetails(ADMIN_MAIL)
//    void setTimeZone() throws Exception {
//        perform(MockMvcRequestBuilders.post(REST_URL + "/time_zone")
//                .param("timeZone", "Asia/Yekaterinburg"))
//                .andDo(print());
//        assertEquals("Asia/Yekaterinburg", TimeZone.getDefault().toZoneId().getId());
//    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void getDefaultTimeZone() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/current_time_zone"))
                .andExpect(result -> assertThat(TimeZone.getDefault())
                        .isEqualTo(JsonUtil.readValue(result.getResponse().getContentAsString(), TimeZone.class)))
                .andDo(print());
    }

    @Test
    void voteTwice() throws Exception {
        VOTE_MATCHER.assertMatch(voteRepository.getVoteByUserAndDate(user, DATE), voteUserHarbinNow);
        final LocalTime votingTime = voteService.getVotingTime();
        final MockedStatic<LocalTime> localTimeMockedStaticAnother = fixCurrentTime(votingTime.minusMinutes(15));
        try (localTimeMockedStaticAnother) {
            perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurant_id", String.valueOf(RESTAURANT_HANOY.id())))
                .andExpect(VOTE_MATCHER.contentJson(voteUser_HanoyNow))
                .andDo(print());
        }
        final Vote voteByUserAndDate = voteRepository.getVoteByUserAndDate(user, DATE);
        final Object unproxy = Hibernate.unproxy(voteByUserAndDate.getRestaurant());
        voteByUserAndDate.setRestaurant((Restaurant) unproxy);
        VOTE_MATCHER.assertMatch(voteByUserAndDate, voteUser_HanoyNow);
    }

    private MockedStatic<LocalTime> fixCurrentTime(LocalTime fixedTime) {
        final MockedStatic<LocalTime> localTimeMockedStatic = Mockito.mockStatic(LocalTime.class);
        localTimeMockedStatic.when(LocalTime::now).thenReturn(fixedTime);
        return localTimeMockedStatic;
    }

}