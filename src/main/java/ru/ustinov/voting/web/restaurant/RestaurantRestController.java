package ru.ustinov.voting.web.restaurant;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.to.RestaurantTo;
import ru.ustinov.voting.web.AuthUser;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

import static ru.ustinov.voting.util.validation.ValidationUtil.checkNew;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 02.09.2021
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Tag(name = "Restaurant Controller")
public class RestaurantRestController extends AbstractRestaurantController {

    public static final String REST_URL = "/rest/admin/restaurants";

    @GetMapping(REST_URL + "/{restaurant_id}")
    public Restaurant get(@PathVariable int restaurant_id) {
        return super.get(restaurant_id);
    }

    @GetMapping(REST_URL)
    public List<Restaurant> getAll() {
        return super.getAll();
    }

    @GetMapping(REST_URL + "/with-dishes-by-date")
    public List<Restaurant> getWithDishesByDate(@RequestParam LocalDate date) {
        return super.getWithDishes(date, false);
    }

    @GetMapping(REST_URL + "/without")
    public List<Restaurant> getWithoutDishes(@RequestParam LocalDate date) {
        return super.getWithoutDishes(date);
    }

    @GetMapping("/rest/profile/voting")
    public List<Restaurant> getWithDishesToday() {
        return super.getWithDishes(LocalDate.now(), true);
    }

    @GetMapping("/rest/profile/result")
    public RestaurantTo getResult(TimeZone timeZone) {
        final ZoneId zoneId = timeZone.toZoneId();
        return super.getResult(LocalDate.now(zoneId), LocalTime.now(zoneId));
    }

    @Override
    @DeleteMapping(REST_URL + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @Override
    @PutMapping(value = REST_URL + "/{restaurant_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody Restaurant restaurant, @PathVariable int restaurant_id) {
        super.update(restaurant, restaurant_id);
    }

    @PostMapping(value = REST_URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> createWithLocation(@Valid @RequestBody Restaurant restaurant) {
        checkNew(restaurant);
        Restaurant created = super.create(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }
}
