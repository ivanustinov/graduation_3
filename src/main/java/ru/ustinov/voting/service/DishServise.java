package ru.ustinov.voting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.error.AppException;
import ru.ustinov.voting.error.NotFoundException;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.repository.RestaurantRepository;
import ru.ustinov.voting.util.validation.Util;
import ru.ustinov.voting.util.validation.ValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;
import static ru.ustinov.voting.util.validation.ValidationUtil.assureIdConsistent;
import static ru.ustinov.voting.util.validation.ValidationUtil.checkNotFoundWithId;

@Service
public class DishServise {

    @Autowired
    private DishServise dishServise;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;


    @CacheEvict(value = {"restaurants", "result"}, allEntries = true)
    public void update(Dish dish, int restaurant_id, int id) {
        assureIdConsistent(dish, id);
        checkNotFoundWithId(save(dish, restaurant_id), id);
    }


    @CacheEvict(value = {"restaurants", "result"}, allEntries = true)
    public void delete(int id, int restaurant_id) {
        final Dish dish = get(id, restaurant_id);
        if (dish == null) {
            throw new NotFoundException("error.validationError");
        }
        if (dish.getDate().isBefore(LocalDate.now())) {
            throw new AppException(HttpStatus.CONFLICT, ErrorAttributeOptions.of(MESSAGE), "dish.create_in_the_past");
        }
        dishRepository.delete(id, restaurant_id);
    }


    @CacheEvict(value = {"restaurants", "result"}, allEntries = true)
    public Dish save(Dish dish, int restaurant_id) {
        if (dish.getDate().isBefore(LocalDate.now())) {
            throw new AppException(HttpStatus.CONFLICT, ErrorAttributeOptions.of(MESSAGE), "dish.create_in_the_past");
        }
        if (!dish.isNew() && get(dish.getId(), restaurant_id) == null) {
            return null;
        }
        final Restaurant restaurant = Util.getEntity(restaurantRepository.get(restaurant_id), "restaurant.unexisting", String.valueOf(restaurant_id));
        dish.setRestaurant(restaurant);
        return dishRepository.save(dish);
    }

    public Dish get(Integer id, Integer restaurant_id) {
        return dishRepository.findById(id)
                .filter(dish -> Objects.equals(dish.getRestaurant().getId(), restaurant_id))
                .orElse(null);
    }


    @CacheEvict(value = {"restaurants", "result"}, allEntries = true)
    public void deleteAllByRestaurantAndDate(int restaurant_id, LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new AppException(HttpStatus.CONFLICT, ErrorAttributeOptions.of(MESSAGE), "dish.create_in_the_past");
        }
        dishRepository.deleteAllByRestaurantAndDate(restaurant_id, date);
    }

    @Transactional
    @CacheEvict(value = {"restaurants", "result"}, allEntries = true)
    public void createDishes(int restaurant_id, List<Dish> dishes) {
        for (Dish dish : dishes) {
            dishServise.save(dish, restaurant_id);
        }
    }
}
