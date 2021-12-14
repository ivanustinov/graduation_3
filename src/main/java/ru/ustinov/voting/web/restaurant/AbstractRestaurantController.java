package ru.ustinov.voting.web.restaurant;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import ru.ustinov.voting.error.AppException;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.repository.RestaurantRepository;
import ru.ustinov.voting.service.RestaurantService;
import ru.ustinov.voting.service.VoteService;
import ru.ustinov.voting.to.RestaurantTo;
import ru.ustinov.voting.util.validation.Util;
import ru.ustinov.voting.web.SecurityUtil;
import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.ustinov.voting.util.validation.ValidationUtil.assureIdConsistent;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 09.11.2021
 */

@Slf4j
public abstract class AbstractRestaurantController {

    @Autowired
    private RestaurantService service;

    @Autowired
    private RestaurantRepository repository;

    @Autowired
    private UniqueRestaurantNameValidator uniqueRestaurantNameValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(uniqueRestaurantNameValidator);
    }

    public Restaurant get(int restaurant_id) {
        log.info("get restaurant {} for user {}", restaurant_id, SecurityUtil.authEmail());
        return Util.getEntity(repository.get(restaurant_id), "error.entityWithIdNotFound", String.valueOf(restaurant_id));
    }

    public List<Restaurant> getAll() {
        log.info("get all restaurants for user {}", SecurityUtil.authEmail());
        return repository.getAll();
    }


    public List<Restaurant> getWithDishes(LocalDate date, boolean needCache) {
        log.info("get all restaurants with dishes on {} for user {}", date, SecurityUtil.authEmail());
        return service.getWithDishes(date,  needCache);
    }

    public List<Restaurant> getWithoutDishes(LocalDate date) {
        log.info("get restaurants without dishes on date {} for user {}", date, SecurityUtil.authEmail());
        return repository.getWithoutDishes(date);
    }

    @Cacheable(value = "result")
    public RestaurantTo getResult(LocalDate date, LocalTime time) {
        log.info("get voting result on date {} for user {}", date,  SecurityUtil.authEmail());
        return service.getResult(date, time);
    }

    @CacheEvict(value = {"restaurants", "result"}, allEntries = true)
    public void delete(int restaurant_id) {
        log.info("delete restaurant with id {} by user {}", restaurant_id, SecurityUtil.authEmail());
        repository.deleteExisted(restaurant_id);
    }

    @CacheEvict(value = {"restaurants", "result"}, allEntries = true)
    public void update(Restaurant restaurant, int restaurant_id) {
        log.info("update restaurant {} wih id {}", restaurant, restaurant_id);
        assureIdConsistent(restaurant, restaurant_id);
        repository.save(restaurant);
    }

    public Restaurant create(Restaurant restaurant) {
        log.info("create restaurant {}", restaurant);
        return repository.save(restaurant);
    }
}
