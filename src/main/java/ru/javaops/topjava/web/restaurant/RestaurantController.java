package ru.javaops.topjava.web.restaurant;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaops.topjava.error.NotFoundException;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.repository.RestaurantRepository;
import ru.javaops.topjava.service.RestaurantService;
import ru.javaops.topjava.to.RestaurantTo;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static ru.javaops.topjava.util.validation.ValidationUtil.assureIdConsistent;
import static ru.javaops.topjava.util.validation.ValidationUtil.checkNew;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 02.09.2021
 */
@RestController
@RequestMapping(value = RestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@Tag(name = "Restaurant Controller")
public class RestaurantController {

    public static final String REST_URL = "/admin/restaurants";

    private final RestaurantService restaurantService;

    private final RestaurantRepository repository;

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> get(@PathVariable int id) {
        log.info("get restaurant with id {}", id);
        return ResponseEntity.of(repository.get(id));
    }

    @GetMapping
    public List<Restaurant> getAll() {
        log.info("get all restaurants");
        return repository.getAll();
    }


    @GetMapping("/with-dishes-by-date")
    @Cacheable(cacheNames = "res")
    public List<RestaurantTo> getWithDishesByDate(@RequestParam LocalDate date) {
        log.info("get all restaurants with dishes on {}", date);
        return restaurantService.getWithDishes(date);
    }

    @GetMapping("/with-dishes-votes-by-date")
    @Cacheable(cacheNames = "votes")
    public List<RestaurantTo> getWithDishesAndVotesByDate(@RequestParam LocalDate date) {
        log.info("get all restaurants with dishes on {}", date);
        return restaurantService.getWithVotesAndDishes(date);
    }

    @GetMapping("/with-votes-users-by-date")
    public ResponseEntity<List<RestaurantTo>> getWithVotesAndDishesAndUsersByDate
            (@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date) {
        log.info("get all restaurants with dishes on {}", date);
        return ResponseEntity.of(restaurantService.getWithVotesAndDishesAndUsers(date));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = {"res", "votes"}, allEntries = true)
    public void delete(@PathVariable int id) {
        log.info("delete restaurant with id {}", id);
        restaurantService.delete(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(value = {"res", "votes"}, allEntries = true)
    public void update(@Valid @RequestBody Restaurant restaurant,
                       @PathVariable int id) {
        log.info("update restaurant {} wih id {}", restaurant, id);
        Assert.notNull(restaurant, "restaurant must not be null");
        assureIdConsistent(restaurant, id);
        repository.get(restaurant.getId()).orElseThrow(() -> new NotFoundException("Такого ресторана не существует"));
        repository.save(restaurant);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = {"res", "votes"}, allEntries = true)
    public ResponseEntity<Restaurant> createWithLocation(@Valid @RequestBody Restaurant restaurant) {
        log.info("create restaurant {}", restaurant);
        checkNew(restaurant);
        Restaurant created = repository.save(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }
}
