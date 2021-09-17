package ru.javaops.topjava.web.user;

import ru.javaops.topjava.MatcherFactory;
import ru.javaops.topjava.model.Role;
import ru.javaops.topjava.model.User;
import ru.javaops.topjava.model.Vote;
import ru.javaops.topjava.web.json.JsonUtil;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTestData {
    public static final MatcherFactory.Matcher<User> MATCHER = MatcherFactory.usingIgnoringFieldsComparator(User.class, "registered", "password", "votes");
    public static MatcherFactory.Matcher<User> WITH_VOTES_MATCHER =
            MatcherFactory.usingAssertions(User.class,
//     No need use ignoringAllOverriddenEquals, see https://assertj.github.io/doc/#breaking-changes
                    (a, e) -> assertThat(a).usingRecursiveComparison()
                            .ignoringFields("registered", "password", "votes.user", "votes.restaurant.dishes", "votes.restaurant.votes").isEqualTo(e),
                    (a, e) -> {
                        throw new UnsupportedOperationException();
                    });

    public static final int USER_ID = 1;
    public static final int ADMIN_ID = 2;
    public static final int USER_2_ID = 3;
    public static final int NOT_FOUND = 100;
    public static final String USER_MAIL = "user@yandex.ru";
    public static final String USER_2_MAIL = "user_2@gmail.ru";
    public static final String ADMIN_MAIL = "admin@gmail.com";

    public static final User user = new User(USER_ID, "User", USER_MAIL, null, Role.USER);
    public static final User user_2 = new User(USER_2_ID, "User2", USER_2_MAIL, null, Role.USER);
    public static final User admin = new User(ADMIN_ID, "Admin", ADMIN_MAIL, null, Role.ADMIN, Role.USER);

    public static User getUserWithVotes(User user, List<Vote> votes) {
        user.setVotes(votes);
        return user;
    }

    public static User getNew() {
        return new User(null, "New", "new@gmail.com", "newPass", Collections.singleton(Role.USER));
    }

    public static User getUpdated() {
        return new User(USER_ID, "UpdatedName", USER_MAIL, "newPass", List.of(Role.ADMIN));
    }

    public static String jsonWithPassword(User user, String passw) {
        return JsonUtil.writeAdditionProps(user, "password", passw);
    }
}
