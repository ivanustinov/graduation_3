package ru.ustinov.voting.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.repository.RestaurantRepository;
import ru.ustinov.voting.to.MenusTo;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * //TODO add comments.
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 21.10.2021
 */
@Service
@AllArgsConstructor
public class MenuServise {

    private DishRepository dishRepository;

    private DishServise dishServise;

    private RestaurantRepository restaurantRepository;

    @Transactional
    public List<MenusTo> getAll() {
        final List<LocalDate> dates = dishRepository.getDates();
        return dates.stream().map(date -> new MenusTo(restaurantRepository.getRestaurantsByDate(date), date)).toList();
    }

    public void delete(LocalDate date) {
        dishRepository.deleteAllByDate(date);
    }

    @Transactional
    public void copy(LocalDate date) {
        final LocalDate now = LocalDate.now();
        final LocalDate lastDishesDate = dishRepository.getLastDishesDate();
        final LocalDate newDate = lastDishesDate.isBefore(now) ? now : lastDishesDate.plusDays(1);
        final List<Dish> dishesByDate = dishRepository.getDishesByDate(date);
        dishesByDate.forEach(dish -> {
            final Dish dish1 = new Dish(null, dish.getName(), newDate, dish.getPrice());
             dishServise.save(dish1, dish.getRestaurant().getId());
        });
    }
}
