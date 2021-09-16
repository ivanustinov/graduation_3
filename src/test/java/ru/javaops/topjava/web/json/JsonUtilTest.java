package ru.javaops.topjava.web.json;

import org.junit.jupiter.api.Test;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.model.User;
import ru.javaops.topjava.web.AbstractControllerTest;
import ru.javaops.topjava.web.user.UserTestData;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.javaops.topjava.web.restaurant.RestaurantTestData.*;

class JsonUtilTest extends AbstractControllerTest {


    @Test
    void readWriteValue() {
        final String json = JsonUtil.writeValue(RESTAURANT_HARBIN);
        System.out.println(json);
        final Restaurant restaurant = JsonUtil.readValue(json, Restaurant.class);
        WITH_VOTES_DISHES_MATCHER.assertMatch(restaurant, RESTAURANT_HARBIN);
    }


    @Test
    void readWriteValues() {
        final List<Restaurant> restaurantsBefore = List.of(RESTAURANT_HARBIN, RESTAURANT_CI);
        String json = JsonUtil.writeValue(restaurantsBefore);
        List<Restaurant> restaurantsAfter = JsonUtil.readValues(json, Restaurant.class);
        WITH_VOTES_DISHES_MATCHER.assertMatch(restaurantsAfter, restaurantsBefore);
    }

    @Test
    void writeOnlyAccess() {
        String json = JsonUtil.writeValue(UserTestData.user);
        System.out.println(json);
        assertThat(json, not(containsString("password")));
        String jsonWithPass = UserTestData.jsonWithPassword(UserTestData.user, "newPass");
        System.out.println(jsonWithPass);
        User user = JsonUtil.readValue(jsonWithPass, User.class);
        assertEquals(user.getPassword(), "newPass");
    }
}