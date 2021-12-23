package ru.ustinov.voting.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.error.AppException;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.repository.RestaurantRepository;
import ru.ustinov.voting.repository.VoteRepository;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 03.09.2021
 */
@Service
@Getter
@Setter
public class VoteService {

    public final static String EXCEPTION_VOTING_AFTER_VOTING_TIME_IS_UP = "voting.time_is_up";

    private final VoteRepository voteRepository;

    private final RestaurantRepository restaurantRepository;

    private LocalTime votingTime = LocalTime.of(12, 0);

    public VoteService(VoteRepository voteRepository, RestaurantRepository restaurantRepository) {
        this.voteRepository = voteRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public Vote vote(User user, int restaurant_id) {
        final LocalTime time = LocalTime.now();
        final Restaurant restaurant = restaurantRepository.getById(restaurant_id);
        final LocalDate date = LocalDate.now();
        if (time.isBefore(votingTime)) {
            Vote vote = voteRepository.getVoteByUserAndDate(user, date);
            if (vote == null) {
                vote = new Vote(restaurant, user, date);
            } else {
                vote.setRestaurant(restaurant);
            }
            return voteRepository.save(vote);
        } else {
            throw new AppException(HttpStatus.CONFLICT, ErrorAttributeOptions.of(MESSAGE),
                    EXCEPTION_VOTING_AFTER_VOTING_TIME_IS_UP);
        }
    }
}
