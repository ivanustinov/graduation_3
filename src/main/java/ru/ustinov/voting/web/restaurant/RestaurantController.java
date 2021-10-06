package ru.ustinov.voting.web.restaurant;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.repository.RestaurantRepository;
import ru.ustinov.voting.service.RestaurantService;
import ru.ustinov.voting.to.RestaurantTo;
import ru.ustinov.voting.util.validation.Util;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static ru.ustinov.voting.util.validation.ValidationUtil.assureIdConsistent;
import static ru.ustinov.voting.util.validation.ValidationUtil.checkNew;

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

//    private AbstractPlatformTransactionManager abstractPlatformTransactionManager;

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
    public List<Restaurant> getWithDishesByDate(@RequestParam LocalDate date) {
        log.info("get all restaurants with dishes on {}", date);
        return restaurantService.getWithDishes(date);
    }

    @GetMapping("/with-dishes-votes-by-date")
    public List<RestaurantTo> getWithDishesAndVotesByDate(@RequestParam LocalDate date) {
        log.info("get all restaurants with dishes on {}", date);
        return restaurantService.getWithVotesAndDishes(date);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = {"restaurants", "votes"}, allEntries = true)
    public void delete(@PathVariable int id) {
        log.info("delete restaurant with id {}", id);
        restaurantService.delete(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(value = {"restaurants", "votes"}, allEntries = true)
    public void update(@Valid @RequestBody Restaurant restaurant,
                       @PathVariable int id) {
        log.info("update restaurant {} wih id {}", restaurant, id);
        Assert.notNull(restaurant, "restaurant must not be null");
        assureIdConsistent(restaurant, id);
        Util.getEntity(repository.get(restaurant.getId()), "Такого ресторана не существует");
        repository.save(restaurant);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = {"restaurants", "votes"}, allEntries = true)
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
