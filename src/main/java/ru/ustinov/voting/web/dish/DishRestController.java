package ru.ustinov.voting.web.dish;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.service.DishServise;
import ru.ustinov.voting.util.validation.ValidationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = DishRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Tag(name = "Dish Controller")
public class DishRestController extends AbstractDishController {

    public static final String REST_URL = "/rest/admin/dishes/{restaurant_id}";

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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int restaurant_id, @PathVariable int id) {
        super.delete(id, restaurant_id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllByRestaurantAndDate(@PathVariable int restaurant_id, @RequestParam LocalDate date) {
        super.deleteAllByRestaurantAndDate(restaurant_id, date);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody Dish dish, @PathVariable int restaurant_id, @PathVariable int id) {
        super.update(dish, restaurant_id, id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> createWithLocation(@Valid @RequestBody Dish dish, @PathVariable int restaurant_id) {
        final Dish created = super.create(dish, restaurant_id);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurant_id, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PostMapping(value = "/lastMenu", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createDishes(@PathVariable int restaurant_id, @Valid @RequestBody ArrayList<Dish> dishes) {
        super.createDishes(restaurant_id, dishes);
    }

}