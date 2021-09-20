package ru.javaops.topjava.web.voting;

import org.w3c.dom.stylesheets.LinkStyle;
import ru.javaops.topjava.MatcherFactory;
import ru.javaops.topjava.model.Vote;

import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
import static java.time.LocalDate.of;
import static ru.javaops.topjava.web.restaurant.RestaurantTestData.RESTAURANT_CI;
import static ru.javaops.topjava.web.restaurant.RestaurantTestData.RESTAURANT_HARBIN;
import static ru.javaops.topjava.web.user.UserTestData.admin;
import static ru.javaops.topjava.web.user.UserTestData.user;

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

    public static final Vote voteUserHarbinNow = new Vote(VOTE_ID, RESTAURANT_HARBIN, user, now());
    public static final Vote voteAdminHarbinNow = new Vote(VOTE_ID + 1, RESTAURANT_HARBIN, admin, now());
    public static final Vote voteUserCi_20150416 = new Vote(VOTE_ID + 2, RESTAURANT_CI, user, of(2015, Month.APRIL, 16));
    public static final Vote voteAdminCi_201501416 = new Vote(VOTE_ID + 3, RESTAURANT_CI, admin, of(2015, Month.APRIL, 16));

    public static final List<Vote> HARBIN_VOTES_NOW = Stream.of(voteUserHarbinNow, voteAdminHarbinNow).sorted(Comparator.comparing(vote -> vote.getId())).toList();
}
