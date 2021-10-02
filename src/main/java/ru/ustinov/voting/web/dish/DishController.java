package ru.ustinov.voting.web.dish;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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
import java.util.List;

@RestController
@RequestMapping(value = DishController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@Tag(name = "Dish Controller")
public class DishController {
    public static final String REST_URL = "/admin/dishes";

    private final DishRepository dishRepository;

    private final DishServise service;

    @GetMapping("/{restaurant_id}/{id}")
    public ResponseEntity<Dish> get(@PathVariable int restaurant_id, @PathVariable int id) {
        log.info("get dish {} in restaurant {}", id, restaurant_id);
        return ResponseEntity.of(dishRepository.get(id));
    }

    @GetMapping("/{restaurant_id}")
    public List<Dish> getDishesByDateAndRestaurant(@PathVariable int restaurant_id, @RequestParam LocalDate date) {
        log.info("get dishes for restaurant {} and date {}", restaurant_id, date);
        return dishRepository.getDishesByDateAndRestaurant(restaurant_id, date);
    }

    @DeleteMapping("/{restaurant_id}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = {"restaurants", "votes"}, allEntries = true)
    public void delete(@PathVariable int id) {
        log.info("delete dish with id {}", id);
        dishRepository.deleteExisted(id);
    }


    @PutMapping(value = "/{restaurant_id}/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody Dish dish,
                       @PathVariable int id, @PathVariable int restaurant_id) {
        log.info("update dish {} for restaurant {}", dish, restaurant_id);
        ValidationUtil.assureIdConsistent(dish, id);
        service.update(dish, restaurant_id);
    }

    @PostMapping(value = "/{restaurant_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> createWithLocation(@Valid @RequestBody Dish dish,
                                                   @PathVariable int restaurant_id) {
        log.info("create dish {} for restaurant_id {}", dish, restaurant_id);
        ValidationUtil.checkNew(dish);
        Dish created = service.save(dish, restaurant_id);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{restaurant_id}/{id}")
                .buildAndExpand(restaurant_id, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }
}