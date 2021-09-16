package ru.javaops.topjava.web.cash;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.topjava.model.NamedEntity;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.repository.DishRepository;
import ru.javaops.topjava.repository.RestaurantRepository;
import ru.javaops.topjava.web.AbstractControllerTest;
import ru.javaops.topjava.web.restaurant.RestaurantTestData;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static ru.javaops.topjava.web.restaurant.RestaurantTestData.*;
import static ru.javaops.topjava.web.user.UserTestData.ADMIN_MAIL;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 10.09.2021
 */
@WithUserDetails(value = ADMIN_MAIL)
public class SpringCacheTest extends AbstractControllerTest {

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    DishRepository dishRepository;

//    @Test
//    public void cacheEvictWhenUpdateRestaurant() {
//        restaurantRepository.getWithDishesByDate(LocalDate.now());
//        restaurantRepository.getWithVotesAndDishesByDate(LocalDate.now());
//        System.out.println("Start Updating");
//        restaurantRepository.save(RestaurantTestData.getUpdated());;
//        final List<Restaurant> restaurants = restaurantRepository.getWithDishesByDate(LocalDate.now()).get();
//        restaurantRepository.getWithVotesAndDishesByDate(LocalDate.now()).get();
//        RESTAURANT_MATCHER.assertMatch(restaurants,
//                Stream.of(RESTAURANT_CI, getUpdated()).sorted(comparing(NamedEntity::getName)).toList());
//    }
//
//
//    @Test
//    public void cacheEvictWhenUpdateDish() {
//        restaurantRepository.getWithDishesByDate(LocalDate.now());
//        restaurantRepository.getWithVotesAndDishesByDate(LocalDate.now());
//        System.out.println("Start Updating Dish");
//        dishRepository.save(getUpdatedDish());
//        final List<Restaurant> withDishesByDate = restaurantRepository.getWithDishesByDate(LocalDate.now()).get();
//        final List<Restaurant> restaurants = restaurantRepository.getWithVotesAndDishesByDate(LocalDate.now()).get();
//        WITH_DISHES_MATCHER.assertMatch(withDishesByDate,
//                Stream.of(RESTAURANT_CI, setUpdatedDish(RESTAURANT_HARBIN, getUpdatedDish())).sorted(comparing(NamedEntity::getName)).toList());
//    }
}


