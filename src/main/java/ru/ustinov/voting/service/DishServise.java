package ru.ustinov.voting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.error.AppException;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.repository.RestaurantRepository;
import ru.ustinov.voting.util.validation.Util;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;
import static ru.ustinov.voting.util.validation.ValidationUtil.checkNotFoundWithId;

@Service
public class DishServise {

    @Autowired
    private DishServise dishServise;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    public void update(Dish dish, int restaurant_id) {
        checkNotFoundWithId(save(dish, restaurant_id), dish.id());
    }

    public Dish save(Dish dish, int restaurant_id) {
        if (dish.getDate().isBefore(LocalDate.now())) {
            throw new AppException(HttpStatus.CONFLICT, messageSourceAccessor.getMessage("dish.create_in_the_past"), ErrorAttributeOptions.of(MESSAGE));
        }
        if (!dish.isNew() && get(dish.getId(), restaurant_id) == null) {
            return null;
        }
        final Restaurant restaurant = Util.getEntity(restaurantRepository.get(restaurant_id),
                messageSourceAccessor.getMessage("restaurant.unexisting", new Integer[]{restaurant_id}));
        dish.setRestaurant(restaurant);
        return dishRepository.save(dish);
    }

    public Dish get(Integer id, Integer restaurant_id) {
        return dishRepository.findById(id)
                .filter(dish -> Objects.equals(dish.getRestaurant().getId(), restaurant_id))
                .orElse(null);
    }

    @Transactional
    public void createDishes(int restaurant_id, List<Dish> dishes) {
        for (Dish dish : dishes) {
            dishServise.save(dish, restaurant_id);
        }
    }
}
