package ru.javaops.topjava.service;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.topjava.error.NotFoundException;
import ru.javaops.topjava.model.Dish;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.model.Vote;
import ru.javaops.topjava.repository.DishRepository;
import ru.javaops.topjava.repository.RestaurantRepository;
import ru.javaops.topjava.repository.VoteRepository;
import ru.javaops.topjava.to.RestaurantTo;
import ru.javaops.topjava.util.DishUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 18.09.2021
 */
@Service
@AllArgsConstructor
@Transactional
public class RestaurantService {

    DishRepository dishRepository;

    RestaurantRepository restaurantRepository;

    VoteRepository voteRepository;


    public List<RestaurantTo> getWithDishes(LocalDate date) {
        final List<Dish> dishesByDate = dishRepository.getDishesByDate(date);
        if (dishesByDate.isEmpty()) {
            throw new NotFoundException("Сегодня нет ресторанов для обслуживания");
        }
        final Map<Integer, List<Dish>> integerListMap = DishUtil.groupByRestaurant(dishesByDate);
        final Set<Integer> restId = integerListMap.keySet();
        final List<Restaurant> restaurants = restaurantRepository.getRestaurantsById(restId);
        return restaurants.stream()
                .map(restaurant -> createTo(restaurant, integerListMap.get(restaurant.getId()))).toList();
    }


    public List<RestaurantTo> getWithVotesAndDishes(LocalDate date) {
        final List<RestaurantTo> withDishes = getWithDishes(date);
        final List<Vote> voteByDateOpt = voteRepository.getVotesByDate(date);
        if (voteByDateOpt.isEmpty()) {
            throw new NotFoundException("За этот день не было голосований");
        }
        final Map<Integer, List<Vote>> votes = voteByDateOpt.stream().collect(
                Collectors.groupingBy((Vote vote) -> vote.getRestaurant().getId(), Collectors.toList()));
        final List<RestaurantTo> restaurantTos = withDishes.stream()
                .peek(restaurantTo -> restaurantTo.setVotes(votes.getOrDefault(restaurantTo.getId(), List.of()))).toList();
        return restaurantTos.stream().sorted(Comparator.comparingInt
                ((ToIntFunction<RestaurantTo>) restaurantTo -> restaurantTo.getVotes().size()).reversed()).toList();
    }


    public Optional<List<RestaurantTo>> getWithVotesAndDishesAndUsers(LocalDate date) {
        final List<RestaurantTo> withDishes = getWithDishes(date);
        final List<Vote> voteByDate = voteRepository.getVoteByDateWithUsers(date);
        if (voteByDate.isEmpty()) {
            throw new NotFoundException("За этот день не было голосований");
        }
        final Map<Integer, List<Vote>> votes = voteByDate.stream().collect(
                Collectors.groupingBy((Vote vote) -> vote.getRestaurant().getId(), Collectors.toList()));
        final List<RestaurantTo> restaurantTos = withDishes.stream().peek(restaurantTo -> restaurantTo
                .setVotes(votes.getOrDefault(restaurantTo.getId(), List.of()))).toList();
        return Optional.of(restaurantTos.stream().sorted(Comparator.comparingInt
                ((ToIntFunction<RestaurantTo>) restaurantTo -> restaurantTo.getVotes().size()).reversed()).toList());
    }


    public void delete(int restaurant_id) {
        final Optional<Restaurant> restaurantOptional = restaurantRepository.get(restaurant_id);
        final Restaurant restaurant = restaurantOptional
                .orElseThrow(() -> new NotFoundException("Ресторана с id " + restaurant_id + " не сущуствует"));
        dishRepository.deleteAllByRestaurant(restaurant);
        voteRepository.deleteAllByRestaurant(restaurant);
        restaurantRepository.deleteById(restaurant_id);
    }

    public static RestaurantTo createTo(Restaurant restaurant, List<Dish> dishes) {
        return new RestaurantTo(restaurant, dishes);
    }

}
