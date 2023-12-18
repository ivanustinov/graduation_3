package ru.ustinov.voting.service;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.error.AppException;
import ru.ustinov.voting.error.NotFoundException;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.repository.RestaurantRepository;
import ru.ustinov.voting.repository.VoteRepository;
import ru.ustinov.voting.to.RestaurantTo;
import ru.ustinov.voting.util.validation.Util;
import ru.ustinov.voting.web.formatter.DateFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 18.09.2021
 */
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    public static final String EXCEPTION_GETTING_RESULT_BEFORE_VOTING_TIME_LEFT = "voting.before_time";

    private final RestaurantRepository restaurantRepository;

    private final VoteRepository voteRepository;

    private final VoteService voteService;

    public static RestaurantTo createTo(Restaurant restaurant, List<Vote> votes) {
        int countVotes = votes == null ? 0 : votes.size();
        return new RestaurantTo(restaurant, countVotes);
    }

    @Cacheable(cacheNames = "restaurants", condition = "#needCache")
    public List<Restaurant> getWithDishes(LocalDate date, boolean needCache) {
        return restaurantRepository.getWithDishes(date);
    }

    @Transactional
    public RestaurantTo getResult(LocalDateTime dateTime) {
        checkResultTime(dateTime.toLocalTime());
        final List<Restaurant> withDishes = getWithDishes(dateTime.toLocalDate(), false);
        final List<Vote> voteByDate = voteRepository.getVotesByDate(dateTime.toLocalDate());
        if (voteByDate.isEmpty()) {
            throw new NotFoundException("voting.no_votes", DateFormatter.format(dateTime.toLocalDate()));
        }
        final Map<Integer, List<Vote>> votes = voteByDate.stream().collect(
                Collectors.groupingBy((Vote vote) -> vote.getRestaurant().getId(), Collectors.toList()));
        final List<RestaurantTo> restaurantTos = withDishes.stream()
                .map(restaurant -> createTo(restaurant, votes.get(restaurant.getId()))).toList();
        final Optional<RestaurantTo> restaurant = restaurantTos.stream().max(Comparator.comparing(RestaurantTo::getCountVotes)
                .thenComparing(Comparator.comparing(RestaurantTo::getName).reversed()));
        return Util.getEntity(restaurant, "Today no result");
    }

    private void checkResultTime(LocalTime time) {
        if (time.isBefore(voteService.getVotingTime())) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorAttributeOptions.of(MESSAGE), EXCEPTION_GETTING_RESULT_BEFORE_VOTING_TIME_LEFT);
        }
    }

    public Restaurant getRestaurant(int restaurant_id) {
        return Util.getEntity(restaurantRepository.get(restaurant_id), "restaurant.unexisting", String.valueOf(restaurant_id));
    }

}
