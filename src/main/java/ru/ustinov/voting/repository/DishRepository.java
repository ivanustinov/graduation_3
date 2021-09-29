package ru.ustinov.voting.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.model.Dish;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface DishRepository extends BaseRepository<Dish> {



    @Query("SELECT d FROM Dish d WHERE d.date = :date ORDER BY d.name")
    List<Dish> getDishesByDate(LocalDate date);

    @Query("SELECT d FROM Dish d WHERE d.date =:date and d.restaurant.id = :restaurant_id ORDER BY d.name")
    List<Dish> getDishesByDateAndRestaurant(int restaurant_id, LocalDate date);

    @Query("select d from Dish d where d.restaurant.id = ?1")
    List<Dish> getDishesByRestaurant(int restaurant_id);

    @Query("SELECT d FROM Dish d WHERE d.id = :id")
    Optional<Dish> get(int id);

    @EntityGraph(attributePaths = {"restaurant"})
    @Query("SELECT d FROM Dish d ORDER BY d.date DESC, d.name")
    List<Dish> getAll();


    @Override
    @Modifying
    @Transactional
    @CacheEvict(value = {"restaurants", "votes"}, allEntries = true)
    Dish save(Dish dish);
}