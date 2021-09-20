package ru.javaops.topjava.util;

import lombok.experimental.UtilityClass;
import ru.javaops.topjava.model.Dish;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class DishUtil {

//    public static List<MealTo> getTos(Collection<Dish> meals, int caloriesPerDay) {
//        return filterByPredicate(meals, caloriesPerDay, meal -> true);
//    }
//
//    public static List<MealTo> getFilteredTos(Collection<Dish> meals, int caloriesPerDay, LocalTime startTime, LocalTime endTime) {
//        return filterByPredicate(meals, caloriesPerDay, meal -> Util.isBetweenHalfOpen(meal.getTime(), startTime, endTime));
//    }

    public static Map<Integer, List<Dish>> groupByRestaurant(Collection<Dish> dishes) {
        return dishes.stream().collect(
                        Collectors.groupingBy((Dish dish1) -> dish1.getRestaurant().getId(), Collectors.toList())
                );
    }

//    public static MealTo createTo(Dish meal, boolean excess) {
//        return new MealTo(meal.getId(), meal.getDate(), meal.getDescription(), meal.getCalories(), excess);
//    }
}
