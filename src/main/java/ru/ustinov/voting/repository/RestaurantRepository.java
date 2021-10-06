package ru.ustinov.voting.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.model.Restaurant;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 02.09.2021
 */

@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant> {

    @Query("SELECT r FROM Restaurant r WHERE r.id = :id")
    Optional<Restaurant> get(int id);

    @Query("SELECT r FROM Restaurant r ORDER BY r.name")
    List<Restaurant> getAll();


    @EntityGraph(attributePaths = {"dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT r FROM Restaurant r join r.dishes d on d.date = :date ORDER BY r.name")
    List<Restaurant> getWithDishes(LocalDate date);


    @Override
    @Modifying
    @Transactional
    Restaurant save(Restaurant restaurant);

    @Override
    @Modifying
    @Transactional
    void deleteById(Integer id);

}
