package ru.javaops.topjava.repository;

import org.springframework.cache.annotation.CacheEvict;
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


    @Query("SELECT d FROM Dish d ORDER BY d.date DESC, d.name")
    List<Dish> getAll();

    //    @Query("SELECT d FROM Dish d WHERE d.restaurant.id=:restaurant_id AND d.date = :date ORDER BY d.name")
    List<Dish> getDishesByRestaurantAndDate(Restaurant restaurant, LocalDate date);


    @Query("SELECT d FROM Dish d WHERE d.id = :id")
    Optional<Dish> get(int id);

    @Override
    @Modifying
    @Transactional
    @CacheEvict(value = {"res", "votes"}, allEntries = true)
    Dish save(Dish dish);
}