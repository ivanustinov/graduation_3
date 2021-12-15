package ru.ustinov.voting.web.dish;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.ustinov.voting.model.Dish;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 22.10.2021
 */
@Hidden
@RestController
@RequestMapping(value = "/admin/dishes/{restaurant_id}", produces = MediaType.APPLICATION_JSON_VALUE)
public class DishUIController extends AbstractDishController {

    @GetMapping("/{id}")
    public Dish get(@PathVariable int id) {
        return super.get(id);
    }

    @GetMapping
    public List<Dish> getDishesByDateAndRestaurant(@PathVariable int restaurant_id, @RequestParam LocalDate date) {
        return super.getDishByDateAndRestaurant(restaurant_id, date);
    }

    @GetMapping("/lastMenu")
    public List<Dish> getLastMenu(@PathVariable int restaurant_id, @RequestParam LocalDate date) {
        return super.getRestaurantsLastMenu(restaurant_id, date);
    }

    @GetMapping("/lastMenuDate")
    public LocalDate getLastMenuDate(@PathVariable int restaurant_id, @RequestParam LocalDate date) {
        return super.getRestaurantLastMenuDate(restaurant_id, date);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int restaurant_id, @PathVariable int id) {
        super.delete(id, restaurant_id);
    }

    @Override
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllByRestaurantAndDate(@PathVariable int restaurant_id, @RequestParam LocalDate date) {
        super.deleteAllByRestaurantAndDate(restaurant_id, date);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createOrUpdate(@PathVariable int restaurant_id, @Valid Dish dish) {
        if (dish.isNew()) {
            super.create(dish, restaurant_id);
        } else {
            super.update(dish, restaurant_id, dish.getId());
        }
    }

    @PostMapping(value = "/lastMenu", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void create(@PathVariable int restaurant_id, @Valid @RequestBody ArrayList<Dish> dishes) {
        super.createDishes(restaurant_id, dishes);
    }
}
