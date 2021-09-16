package ru.javaops.topjava.web.dish;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaops.topjava.model.Dish;
import ru.javaops.topjava.repository.DishRepository;
import ru.javaops.topjava.service.DishServise;
import ru.javaops.topjava.web.AuthUser;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.javaops.topjava.util.validation.ValidationUtil.assureIdConsistent;
import static ru.javaops.topjava.util.validation.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = DishController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class DishController {
    static final String REST_URL = "/admin/dishes";

    private final DishRepository repository;
    private final DishServise service;

    @GetMapping("/{id}")
    public ResponseEntity<Dish> get(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("get dish {} for user {}", id, authUser.id());
        return ResponseEntity.of(repository.get(id));
    }

    @GetMapping
    public List<Dish> getAll() {
        log.info("get all dishes");
        return repository.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("delete dish with id {}", id);
        repository.deleteExisted(id);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody Dish dish,
                       @PathVariable int id, @RequestParam int restaurant_id) {
        log.info("update dish {} for restaurant {}", dish, restaurant_id);
        assureIdConsistent(dish, id);
        service.update(dish, restaurant_id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> createWithLocation(@Valid @RequestBody Dish dish,
                                                   @RequestParam int restaurant_id) {
        log.info("create dish {} for restaurant_id {}", dish, restaurant_id);
        checkNew(dish);
        Dish created = service.save(dish, restaurant_id);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }
}