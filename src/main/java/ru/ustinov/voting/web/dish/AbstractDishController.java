package ru.ustinov.voting.web.dish;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import ru.ustinov.voting.error.AppException;
import ru.ustinov.voting.error.NotFoundException;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.service.DishServise;
import ru.ustinov.voting.util.validation.ValidationUtil;
import ru.ustinov.voting.web.SecurityUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;
import static ru.ustinov.voting.util.validation.ValidationUtil.checkNew;
import static ru.ustinov.voting.util.validation.ValidationUtil.checkNotFoundWithId;

/**
 * //TODO add comments.
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 09.11.2021
 */

@Slf4j
public abstract class AbstractDishController {

    @Autowired
    private DishServise dishServise;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private UniqueNameDateRestaurantValidator uniqueNameDateRestaurantValidator;

    @Autowired
    private DishListUniqueNameDateValidator dishListUniqueNameDateValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        final Optional<Object> validator = Optional.ofNullable(binder.getTarget()).filter(field -> Dish.class.isAssignableFrom(field.getClass()));
        if (validator.isPresent()) {
            binder.addValidators(uniqueNameDateRestaurantValidator);
        } else {
            binder.addValidators(dishListUniqueNameDateValidator);
        }
    }

    public Dish get(int id) {
        log.info("get dish {} for user {}", id, SecurityUtil.authEmail());
        return dishRepository.get(id).orElseThrow(() -> new NotFoundException("dish with id = " + id + " not found"));
    }

    public List<Dish> getDishByDateAndRestaurant(int restaurant_id, LocalDate date) {
        log.info("get dishes for restaurant {} and date {} for user {}", restaurant_id, date, SecurityUtil.authEmail());
        return dishRepository.getDishesByDateAndRestaurant(restaurant_id, date);
    }

    public List<Dish> getRestaurantsLastMenu(int restaurant_id, LocalDate date) {
        log.info("get dishes with restaurant_id {} and date {} by user {}", restaurant_id, date, SecurityUtil.authEmail());
        return dishRepository.getLastMenu(restaurant_id, date);
    }

    public LocalDate getRestaurantLastMenuDate(int restaurant_id, LocalDate date) {
        log.info("get last menu date for restaurant_id {} and date {} by user {}", restaurant_id, date, SecurityUtil.authEmail());
        return dishRepository.getLastMenuDate(restaurant_id, date);
    }

    @CacheEvict(value = {"restaurants", "result"}, allEntries = true)
    public void deleteAllByRestaurantAndDate(int restaurant_id, LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new AppException(HttpStatus.OK, "dish.create_in_the_past", ErrorAttributeOptions.of(MESSAGE));
        }
        log.info("delete dishes with restaurant {} and date {} by user {}", restaurant_id, date, SecurityUtil.authEmail());
        dishRepository.deleteAllByRestaurantAndDate(restaurant_id, date);
    }

    @CacheEvict(value = {"restaurants", "result"}, allEntries = true)
    public void delete(int id, int restaurant_id) {
        log.info("delete dish with id {} by user {}", id, SecurityUtil.authEmail());
        checkNotFoundWithId(dishRepository.delete(id, restaurant_id) == 0, id);
    }

    @CacheEvict(value = {"restaurants", "result"}, allEntries = true)
    public void update(Dish dish, int restaurant_id, int id) {
        log.info("update {} for restaurant {} by user {}", dish, restaurant_id, SecurityUtil.authEmail());
        ValidationUtil.assureIdConsistent(dish, id);
        dishServise.update(dish, restaurant_id);
    }

    @CacheEvict(value = {"restaurants", "result"}, allEntries = true)
    public Dish create(Dish dish, int restaurant_id) {
        log.info("create {} for restaurant {} by user {}", dish, restaurant_id, SecurityUtil.authEmail());
        checkNew(dish);
        return dishServise.save(dish, restaurant_id);
    }

    @CacheEvict(value = {"restaurants", "result"}, allEntries = true)
    public void createDishes(int restaurant_id, ArrayList<Dish> dishes) {
        log.info("create dishes {} for restaurant {} by user {}", dishes, restaurant_id, SecurityUtil.authEmail());
        dishServise.createDishes(restaurant_id, dishes);
    }
}
