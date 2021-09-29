package ru.ustinov.voting.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.ustinov.voting.error.NotFoundException;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.repository.RestaurantRepository;
import ru.ustinov.voting.util.validation.Util;

import javax.persistence.EntityManager;
import java.time.LocalDate;

@Service
@AllArgsConstructor
public class DishServise {


    private final DishRepository dishRepository;

    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void update(Dish dish, int restaurant_id) {
        Assert.notNull(dish, "dish must not be null");
        final Dish dishDb = Util.getEntity(dishRepository.get(dish.getId()),"Такого блюда не существует");
        dish.setRestaurant(restaurantRepository.getById(restaurant_id));
        dish.setDate(dishDb.getDate());
        dishRepository.save(dish);
    }

    public Dish save(Dish dish, int restaurant_id) {
        Assert.notNull(dish, "dish must not be null");
        dish.setDate(LocalDate.now());
        dish.setRestaurant(restaurantRepository.getById(restaurant_id));
        return dishRepository.save(dish);
    }
}
