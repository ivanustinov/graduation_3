package ru.ustinov.voting.web.restaurant;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.to.RestaurantTo;

import java.time.LocalDate;
import java.util.List;

/**
 * //TODO add comments.
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 20.10.2021
 */

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantUIController extends AbstractRestaurantController {

    public static final String REST_URL = "/admin/restaurants";

    @GetMapping(REST_URL + "/{id}")
    public Restaurant get(@PathVariable int id) {
        return super.get(id);
    }

    @GetMapping(REST_URL)
    public List<Restaurant> getAll() {
        return super.getAll();
    }

    @GetMapping(REST_URL + "/with-dishes-by-date")
    public List<Restaurant> getWithDishesByDate(@RequestParam LocalDate date) {
        return super.getWithDishes(date);
    }

    @GetMapping(REST_URL + "/without")
    public List<Restaurant> getWithoutDishes(LocalDate date) {
        return super.getWithoutDishes(date);
    }

    @GetMapping("/profile/result")
    public RestaurantTo getResult() {
        return super.getResult(LocalDate.now());
    }

    @DeleteMapping(REST_URL + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @PostMapping(REST_URL)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createOrUpdate(Restaurant restaurant) {
        if (restaurant.isNew()) {
            super.create(restaurant);
        } else {
            super.update(restaurant, restaurant.getId());
        }
    }

}
