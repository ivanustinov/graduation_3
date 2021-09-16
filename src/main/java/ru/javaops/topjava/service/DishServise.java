package ru.javaops.topjava.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.javaops.topjava.error.NotFoundException;
import ru.javaops.topjava.model.Dish;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.repository.DishRepository;
import ru.javaops.topjava.repository.RestaurantRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class DishServise {


    private final DishRepository dishRepository;

    private final RestaurantRepository restaurantRepository;

    public void update(Dish dish, int restaurant_id) {
        Assert.notNull(dish, "dish must not be null");
        dishRepository.get(dish.getId()).orElseThrow(() -> new NotFoundException("Такого блюда не существует"));
        dish.setRestaurant(getRestaurant(restaurant_id));
        dishRepository.save(dish);
    }

    public Dish save(Dish dish, int restaurant_id) {
        Assert.notNull(dish, "dish must not be null");
        dish.setRestaurant(getRestaurant(restaurant_id));
        return dishRepository.save(dish);
    }

    private Restaurant getRestaurant(int restaurant_id) {
        return restaurantRepository.get(restaurant_id).orElseThrow(
                () -> new NotFoundException("Такого ресторана не существует"));
    }
}
