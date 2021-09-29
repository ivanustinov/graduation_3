package ru.ustinov.voting.repository;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.model.Restaurant;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 02.09.2021
 */

@Transactional(readOnly = true)
@CacheConfig(cacheNames = {"restaurants", "votes"})
public interface RestaurantRepository extends BaseRepository<Restaurant> {

    @Query("SELECT r FROM Restaurant r WHERE r.id = :id")
    Optional<Restaurant> get(int id);

    @Query("SELECT r FROM Restaurant r ORDER BY r.name")
    List<Restaurant> getAll();

    @Query("SELECT r FROM Restaurant r where r.id in :id ORDER BY r.name")
    List<Restaurant> getRestaurantsById(Set<Integer> id);

    @Override
    @Modifying
    @Transactional
    Restaurant save(Restaurant restaurant);

    @Override
    @Modifying
    @Transactional
    void deleteById(Integer id);

}
