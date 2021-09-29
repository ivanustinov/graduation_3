package ru.ustinov.voting.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.error.NotFoundException;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.repository.VoteRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 03.09.2021
 */
@Service
@AllArgsConstructor
public class VoteService {


    private final VoteRepository voteRepository;

    private final EntityManager entityManager;

    private final LocalTime votingTime = LocalTime.of(11, 0);

    //In real life i want to disable opportunity to get results before eleven o'clock.
    // So annotation @CacheEvict can be removed
//    @CacheEvict(value = "votes", allEntries = true)
    @Transactional
    public void vote(User user, int restaurant_id, LocalTime time) {
        if (time.isAfter(votingTime)) {
            throw new NotFoundException("Время для голосования истекло");
        }
        final Restaurant restaurant = entityManager.getReference(Restaurant.class, restaurant_id);
        final LocalDate now = LocalDate.now();
        Vote vote = voteRepository.getVoteByUserAndDate(user, now);
        if (vote != null) {
            vote.setRestaurant(restaurant);
        } else {
            vote = new Vote(restaurant, user, now);
        }
        voteRepository.save(vote);
    }
}
