package ru.javaops.topjava.web.dish;

import ru.javaops.topjava.MatcherFactory;
import ru.javaops.topjava.model.Dish;

import java.util.Iterator;

import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.now;
import static java.time.LocalDate.of;
import static org.assertj.core.api.Assertions.assertThat;

public class DishTestData {
    public static final MatcherFactory.Matcher<Dish> MATCHER = MatcherFactory
            .usingIgnoringFieldsComparator(Dish.class, "restaurant.dishes", "restaurant.votes");
    public static MatcherFactory.Matcher<Dish> WITH_VOTES_DISHES_MATCHER =
            MatcherFactory.usingAssertions(Dish.class,
//     No need use ignoringAllOverriddenEquals, see https://assertj.github.io/doc/#breaking-changes
                    (a, e) -> assertThat(a).usingRecursiveComparison()
                            .ignoringFields("votes.restaurant", "dishes.restaurant").isEqualTo(e),
                    (a, e) -> {
                        final Iterator<Dish> iterator = e.iterator();
                        for (Dish t : a) {
                            final Dish next = iterator.next();
                            assertThat(t).usingRecursiveComparison().isEqualTo(next);
                        }
                    });


}
