package ru.ustinov.voting.web.voting;

import ru.ustinov.voting.MatcherFactory;
import ru.ustinov.voting.model.Vote;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
import static java.time.LocalDate.of;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.*;
import static ru.ustinov.voting.web.user.UserTestData.*;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 11.09.2021
 */
public class VoteTestData {
    public static final MatcherFactory.Matcher<Vote> VOTE_MATCHER = MatcherFactory
            .usingIgnoringFieldsComparator(Vote.class, "id", "user.password", "user.roles", "restaurant.name", "restaurant.dishes");

    public static final int VOTE_ID = 1;
    public static LocalDate now = now();

    public static final Vote voteUserHarbinNow = new Vote(VOTE_ID, RESTAURANT_HARBIN, user, now);
    public static final Vote voteAdminHarbinNow = new Vote(VOTE_ID + 1, RESTAURANT_HARBIN, admin, now);
    public static final Vote voteUserCi_20150416 = new Vote(VOTE_ID + 2, RESTAURANT_CI, user, of(2015, Month.APRIL, 16));
    public static final Vote voteUser_HanoyNow = new Vote(VOTE_ID + 5, RESTAURANT_HANOY, user, now);

    public static final List<Vote> HARBIN_VOTES_NOW = Stream.of(voteUserHarbinNow, voteAdminHarbinNow).sorted(Comparator.comparing(vote -> vote.getId())).toList();
    public static final List<Vote> USER_VOTES = Stream.of(voteUserHarbinNow, voteUserCi_20150416).sorted(Comparator.comparing(Vote::getDate).reversed()).toList();
}
