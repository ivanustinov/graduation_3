package ru.javaops.topjava.service;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.topjava.error.NotFoundException;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.model.User;
import ru.javaops.topjava.model.Vote;
import ru.javaops.topjava.repository.RestaurantRepository;
import ru.javaops.topjava.repository.VoteRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private final RestaurantRepository restaurantRepository;
    private final VoteRepository voteRepository;

    //In real life i want to disable opportunity to get results before eleven o'clock.
    // So annotation @CacheEvict can be removed
//    @CacheEvict(value = "votes", allEntries = true)
    public void vote(User user, int restaurant_id, LocalTime time) {
        if (time.isAfter(LocalTime.of(11, 0))) {
            throw new NotFoundException("Время для голосования истекло");
        }
        final Restaurant restaurant = restaurantRepository.get(restaurant_id).orElseThrow(
                () -> new NotFoundException("Такого ресторана не существует"));
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
