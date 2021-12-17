package ru.ustinov.voting.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.model.Restaurant;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 02.09.2021
 */

@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant> {

    @Query("SELECT r FROM Restaurant r WHERE r.id = :id")
    Optional<Restaurant> get(int id);

    @Query("select r from Restaurant r where r.name = ?1")
    Optional<Restaurant> getRestaurantByName(String name);

    @Query("select r from Restaurant r WHERE r.name = :name")
    Optional<Restaurant> getByName(String name);

    @Query("SELECT r FROM Restaurant r ORDER BY r.name")
    List<Restaurant> getAll();

    @EntityGraph(attributePaths = {"dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT r FROM Restaurant r join r.dishes d on d.date = :date ORDER BY r.name")
    List<Restaurant> getWithDishes(LocalDate date);

    @Query("SELECT distinct r FROM Restaurant r join r.dishes d on d.date = :date ORDER BY r.name")
    List<Restaurant> getRestaurantsByDate(LocalDate date);

    @Query("select r from Restaurant r left join r.dishes d on d.date =:date where d.name is null")
    List<Restaurant> getWithoutDishes(LocalDate date);

    @Query("select d.restaurant from Dish d where d.id =:id")
    Optional<Restaurant> getDishRestaurant(int id);

//    @Query("select r, count(v) from Vote v join v.restaurant r where v.date=:date group by r")
//    List<RestaurantTo> getResult(LocalDate date);

}
