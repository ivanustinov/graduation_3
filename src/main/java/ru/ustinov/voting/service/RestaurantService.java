package ru.ustinov.voting.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.support.MessageSourceAccessor;
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

import java.time.LocalDate;
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

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    @Cacheable(cacheNames = "restaurants")
    public List<Restaurant> getWithDishes(LocalDate date) {
        return restaurantRepository.getWithDishes(date);
    }

    @Transactional
    public RestaurantTo getResult(LocalDate date) {
        checkResultTime();
        final List<Restaurant> withDishes = getWithDishes(date);
        final List<Vote> voteByDate = voteRepository.getVotesByDate(date);
        if (voteByDate.isEmpty()) {
            throw new NotFoundException(messageSourceAccessor.getMessage("voting.no_votes", new LocalDate[]{date}));
        }
        final Map<Integer, List<Vote>> votes = voteByDate.stream().collect(
                Collectors.groupingBy((Vote vote) -> vote.getRestaurant().getId(), Collectors.toList()));
        final List<RestaurantTo> restaurantTos = withDishes.stream()
                .map(restaurant -> createTo(restaurant, votes.get(restaurant.getId()))).toList();
        final Optional<RestaurantTo> restaurant = restaurantTos.stream().max(Comparator.comparing(RestaurantTo::getCountVotes)
                .thenComparing(Comparator.comparing(RestaurantTo::getName).reversed()));
        return Util.getEntity(restaurant, "Today no result");
    }

    private void checkResultTime() {
        final LocalTime votingTime = voteService.getVotingTime();
        if ((LocalTime.now().isBefore(votingTime))) {
            throw new AppException(HttpStatus.BAD_REQUEST, messageSourceAccessor.getMessage(EXCEPTION_GETTING_RESULT_BEFORE_VOTING_TIME_LEFT), ErrorAttributeOptions.of(MESSAGE));
        }
    }

    public Restaurant getRestaurant(int restaurant_id) {
        return Util.getEntity(restaurantRepository.get(restaurant_id), messageSourceAccessor.getMessage("restaurant.unexisting", new Integer[]{restaurant_id}));
    }

    public static RestaurantTo createTo(Restaurant restaurant, List<Vote> votes) {
        int countVotes = votes == null ? 0 : votes.size();
        return new RestaurantTo(restaurant, countVotes);
    }

}
