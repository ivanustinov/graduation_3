package ru.javaops.topjava.repository;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.topjava.model.Restaurant;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * //TODO add comments.
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 02.09.2021
 */

@Transactional(readOnly = true)
@CacheConfig(cacheNames = {"res", "votes"})
public interface RestaurantRepository extends BaseRepository<Restaurant> {

    @Query("SELECT r FROM Restaurant r WHERE r.id = :id")
    Optional<Restaurant> get(int id);

    @Query("SELECT r FROM Restaurant r ORDER BY r.name")
    List<Restaurant> getAll();

    @Query("SELECT r FROM Restaurant r where r.id in :id ORDER BY r.name")
    List<Restaurant> getRestaurantsById(Set<Integer> id);

//    @Cacheable(cacheNames = "res")
//    @EntityGraph(attributePaths = {"dishes"}, type = EntityGraph.EntityGraphType.LOAD)
//    @Query("SELECT r FROM Restaurant r JOIN r.dishes dh on dh.date =:date order by r.name")
//    Optional<List<Restaurant>> getWithDishesByDate(LocalDate date);

//    @Cacheable(cacheNames = "votes")
//    @EntityGraph(attributePaths = {"dishes", "votes"}, type = EntityGraph.EntityGraphType.LOAD)
//    @Query("SELECT r FROM Restaurant r JOIN r.dishes d on d.date =:date left JOIN r.votes v on v.date =:date order by v.size desc ")
//    Optional<List<Restaurant>> getWithVotesAndDishesByDate(LocalDate date);


//    @EntityGraph(attributePaths = {"dishes", "votes", "votes.user"}, type = EntityGraph.EntityGraphType.LOAD)
//    @Query("SELECT r FROM Restaurant r JOIN r.dishes dh on dh.date =:date left JOIN r.votes v on v.date =:date order by v.size desc ")
//    Optional<List<Restaurant>> getWithVotesAndDishesAndUsersByDate(LocalDate date);


    @Override
    @Modifying
    @Transactional
    @CacheEvict(value = {"res", "votes"}, allEntries = true)
    Restaurant save(Restaurant restaurant);



    @Override
    @Modifying
    @Transactional
    void deleteById(Integer id);

}
