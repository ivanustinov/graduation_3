package ru.ustinov.voting.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.error.NotFoundException;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.repository.RestaurantRepository;
import ru.ustinov.voting.repository.VoteRepository;
import ru.ustinov.voting.to.RestaurantTo;
import ru.ustinov.voting.util.DishUtil;
import ru.ustinov.voting.util.validation.Util;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 18.09.2021
 */
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
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
                .peek(restaurantTo -> restaurantTo.setVotes(votes.getOrDefault(restaurantTo.getId(), List.of()).size())).toList();
        return restaurantTos.stream().sorted(Comparator.comparing(RestaurantTo::getVotes).reversed()).toList();
    }


    @Transactional
    public void delete(int restaurant_id) {
        final Optional<Restaurant> restaurantOptional = restaurantRepository.get(restaurant_id);
        Util.getEntity(restaurantOptional, "Ресторана с id " + restaurant_id + " не сущуствует");
        restaurantRepository.deleteById(restaurant_id);
    }

    public static RestaurantTo createTo(Restaurant restaurant, List<Dish> dishes) {
        return new RestaurantTo(restaurant, dishes);
    }

}
