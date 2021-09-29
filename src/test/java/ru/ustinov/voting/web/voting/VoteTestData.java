package ru.ustinov.voting.web.voting;

import ru.ustinov.voting.MatcherFactory;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.web.restaurant.RestaurantTestData;
import ru.ustinov.voting.web.user.UserTestData;

import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
import static java.time.LocalDate.of;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 11.09.2021
 */
public class VoteTestData {
    public static final MatcherFactory.Matcher<Vote> VOTE_MATCHER = MatcherFactory
            .usingIgnoringFieldsComparator(Vote.class, "restaurant.dishes", "restaurant.votes",
                    "user.votes", "user.password", "user.roles");

    public static final int VOTE_ID = 1;

    public static final Vote voteUserHarbinNow = new Vote(VOTE_ID, RestaurantTestData.RESTAURANT_HARBIN, UserTestData.user, now());
    public static final Vote voteAdminHarbinNow = new Vote(VOTE_ID + 1, RestaurantTestData.RESTAURANT_HARBIN, UserTestData.admin, now());
    public static final Vote voteUserCi_20150416 = new Vote(VOTE_ID + 2, RestaurantTestData.RESTAURANT_CI, UserTestData.user, of(2015, Month.APRIL, 16));
    public static final Vote voteAdminCi_201501416 = new Vote(VOTE_ID + 3, RestaurantTestData.RESTAURANT_CI, UserTestData.admin, of(2015, Month.APRIL, 16));

    public static final List<Vote> HARBIN_VOTES_NOW = Stream.of(voteUserHarbinNow, voteAdminHarbinNow).sorted(Comparator.comparing(vote -> vote.getId())).toList();
    public static final List<Vote> USER_VOTES = Stream.of(voteUserHarbinNow, voteUserCi_20150416).sorted(Comparator.comparing(vote -> vote.getId())).toList();
}
