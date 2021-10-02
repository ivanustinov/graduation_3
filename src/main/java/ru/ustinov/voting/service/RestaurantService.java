package ru.ustinov.voting.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.error.NotFoundException;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.repository.DishRepository;
import ru.ustinov.voting.repository.RestaurantRepository;
import ru.ustinov.voting.repository.VoteRepository;
import ru.ustinov.voting.to.RestaurantTo;
import ru.ustinov.voting.util.validation.Util;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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


    public List<Restaurant> getWithDishes(LocalDate date) {
        final List<Restaurant> withDishes = restaurantRepository.getWithDishes(date);
        if (withDishes.isEmpty()) {
            throw new NotFoundException("Сегодня нет ресторанова на обслуживании.");
        }
        return withDishes;
    }


    public List<RestaurantTo> getWithVotesAndDishes(LocalDate date) {
        final List<Restaurant> withDishes = getWithDishes(date);
        final List<Vote> voteByDateOpt = voteRepository.getVotesByDate(date);
        if (voteByDateOpt.isEmpty()) {
            throw new NotFoundException("За этот день не было голосований");
        }
        final Map<Integer, List<Vote>> votes = voteByDateOpt.stream().collect(
                Collectors.groupingBy((Vote vote) -> vote.getRestaurant().getId(), Collectors.toList()));
        final List<RestaurantTo> restaurantTos = withDishes.stream()
                .map(restaurant -> createTo(restaurant, votes.get(restaurant.getId()))).toList();
        return restaurantTos.stream().sorted(Comparator.comparing(RestaurantTo::getCountVotes).reversed()).toList();
    }


    @Transactional
    public void delete(int restaurant_id) {
        final Optional<Restaurant> restaurantOptional = restaurantRepository.get(restaurant_id);
        Util.getEntity(restaurantOptional, "Ресторана с id " + restaurant_id + " не сущуствует");
        restaurantRepository.deleteById(restaurant_id);
    }

    public static RestaurantTo createTo(Restaurant restaurant, List<Vote> votes) {
        int countVotes = votes == null ? 0 : votes.size();
        return new RestaurantTo(restaurant, countVotes);
    }

}
