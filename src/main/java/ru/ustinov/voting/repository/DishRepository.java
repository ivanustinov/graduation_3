package ru.ustinov.voting.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.model.Restaurant;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface DishRepository extends BaseRepository<Dish> {

    @Query("SELECT d FROM Dish d WHERE d.date = :date ORDER BY d.name")
    List<Dish> getDishesByDate(LocalDate date);

    @Query("SELECT d FROM Dish d WHERE d.restaurant.id=:restaurant_id and d.date =:date and d.name =:name")
    Optional<Dish> getDishByDateAndNameAndRestaurant(LocalDate date, String name, Integer restaurant_id);

    @Modifying
    @Transactional
    @Query("delete from Dish d where d.restaurant.id = ?1 and d.date = ?2")
    void deleteAllByRestaurantAndDate(int restaurant_id, LocalDate date);

    @Modifying
    @Transactional
    @Query("delete from Dish d where d.date =:date")
    void deleteAllByDate(LocalDate date);

    @Modifying
    @Transactional
    @Query("delete from Dish d where d.id =:id and d.restaurant.id =:restaurant_id")
    int delete(int id, int restaurant_id);

    @Query("SELECT d FROM Dish d WHERE d.date =:date and d.restaurant.id = :restaurant_id ORDER BY d.name")
    List<Dish> getDishesByDateAndRestaurant(int restaurant_id, LocalDate date);

    @Query("select d from Dish d where d.restaurant.id = ?1")
    List<Dish> getDishesByRestaurant(int restaurant_id);

    @Query("SELECT d FROM Dish d WHERE d.id = :id")
    Optional<Dish> get(int id);

    @EntityGraph(attributePaths = {"restaurant"})
    @Query("SELECT d FROM Dish d ORDER BY d.date DESC, d.name")
    List<Dish> getAll();

    @Query("SELECT distinct d.date FROM Dish d ORDER BY d.date DESC")
    List<LocalDate> getDates();

    @Query("SELECT max(d.date) FROM Dish d")
    LocalDate getLastDishesDate();

    @Query("select max(d.date) from Dish d where d.restaurant.id =:restaurant_id and d.date <:date")
    LocalDate getLastMenuDate(int restaurant_id, LocalDate date);

    @Query("SELECT d.restaurant from Dish d where d.date =:date")
    List<Restaurant> getRestaurants(LocalDate date);

    @Query("select d from Dish d where d.date =(select max(d.date) from Dish d where d.restaurant.id =:restaurant_id and d.date <:date) and d.restaurant.id =:restaurant_id order by d.name")
    List<Dish> getLastMenu(int restaurant_id, LocalDate date);

}