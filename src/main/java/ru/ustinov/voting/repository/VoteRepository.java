package ru.ustinov.voting.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.model.Vote;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 03.09.2021
 */
public interface VoteRepository extends BaseRepository<Vote> {

    @EntityGraph(attributePaths = {"restaurant", "user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select v from Vote v where v.user =:user and v.date =:date")
    Vote getVoteByUserAndDate(User user, LocalDate date);


    @Query("select v from Vote v where v.date = ?1")
    List<Vote> getVotesByDate(LocalDate date);


    @Query("select v from Vote v where v.restaurant.id = ?1")
    List<Vote> getVoteByRestaurant(int restaurant_id);

    @EntityGraph(attributePaths = {"restaurant"})
    @Query("select v from Vote v where v.user =?1 order by v.date desc")
    List<Vote> getVoteByUser(User user);
}
