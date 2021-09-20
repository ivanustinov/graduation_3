package ru.javaops.topjava.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.model.User;
import ru.javaops.topjava.model.Vote;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 03.09.2021
 */
public interface VoteRepository extends BaseRepository<Vote> {

    @EntityGraph(attributePaths = {"restaurant", "user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select v from Vote v where v.user =:user and v.date =:date order by v.date")
    Vote getVoteByUserAndDate(User user, LocalDate date);


    @Query("select v from Vote v where v.date = ?1")
    List<Vote> getVotesByDate(LocalDate date);

    @Modifying
    @Transactional
    @Query("delete from Vote v where v.restaurant = ?1")
    void deleteAllByRestaurant(Restaurant restaurant);

    @EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select v from Vote v where v.date = :date")
    List<Vote> getVoteByDateWithUsers(LocalDate date);
}
