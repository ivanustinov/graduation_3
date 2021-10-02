package ru.ustinov.voting.service;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.error.NotFoundException;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.repository.RestaurantRepository;
import ru.ustinov.voting.repository.VoteRepository;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 03.09.2021
 */
@Service
@AllArgsConstructor
public class VoteService {



    private final VoteRepository voteRepository;

    private final RestaurantRepository restaurantRepository;

    private final LocalTime votingTime = LocalTime.of(11, 0);

    @Transactional
    @CacheEvict(value = "votes", allEntries = true)
    public Vote vote(User user, int restaurant_id) {
        final LocalTime time = LocalTime.now();
        final Restaurant restaurant = restaurantRepository.getById(restaurant_id);
        final LocalDate now = LocalDate.now();
        Vote vote = voteRepository.getVoteByUserAndDate(user, now);
        if (vote != null) {
            vote.setRestaurant(restaurant);
        } else {
            vote = new Vote(restaurant, user, now);
            return voteRepository.save(vote);
        }
        if (time.isAfter(votingTime)) {
            throw new NotFoundException("Время для голосования истекло");
        }
        return voteRepository.save(vote);
    }
}
