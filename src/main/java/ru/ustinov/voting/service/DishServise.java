package ru.ustinov.voting.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.repository.RestaurantRepository;
import ru.ustinov.voting.util.validation.Util;

@Service
@AllArgsConstructor
public class DishServise {

    private final DishRepository dishRepository;

    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void update(Dish dish, int restaurant_id) {
        Assert.notNull(dish, "Dish must not be null");
        Util.getEntity(dishRepository.get(dish.getId()), "Такого блюда не существует");
        dish.setRestaurant(restaurantRepository.getById(restaurant_id));
        dishRepository.save(dish);
    }

    public Dish save(Dish dish, int restaurant_id) {
        Assert.notNull(dish, "Dish must not be null");
        dish.setRestaurant(restaurantRepository.getById(restaurant_id));
        return dishRepository.save(dish);
    }
}
