package ru.javaops.topjava.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.topjava.model.Dish;
import ru.javaops.topjava.model.Restaurant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface DishRepository extends BaseRepository<Dish> {



    @Query("SELECT d FROM Dish d WHERE d.date = :date ORDER BY d.name")
    List<Dish> getDishesByDate(LocalDate date);

    @EntityGraph(attributePaths = {"restaurant"})
    @Query("SELECT d FROM Dish d WHERE d.id = :id")
    Optional<Dish> get(int id);

    @EntityGraph(attributePaths = {"restaurant"})
    @Query("SELECT d FROM Dish d ORDER BY d.date DESC, d.name")
    List<Dish> getAll();


    @Modifying
    @Query("delete from Dish d where d.restaurant = ?1")
    void deleteAllByRestaurant(Restaurant restaurant);

    @Override
    @Modifying
    @Transactional
    @CacheEvict(value = {"res", "votes"}, allEntries = true)
    Dish save(Dish dish);
}