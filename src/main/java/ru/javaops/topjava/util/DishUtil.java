package ru.javaops.topjava.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DishUtil {
//
//    public static List<MealTo> getTos(Collection<Dish> meals, int caloriesPerDay) {
//        return filterByPredicate(meals, caloriesPerDay, meal -> true);
//    }
//
//    public static List<MealTo> getFilteredTos(Collection<Dish> meals, int caloriesPerDay, LocalTime startTime, LocalTime endTime) {
//        return filterByPredicate(meals, caloriesPerDay, meal -> Util.isBetweenHalfOpen(meal.getTime(), startTime, endTime));
//    }

//    public static List<MealTo> filterByPredicate(Collection<Dish> meals, int caloriesPerDay, Predicate<Dish> filter) {
//        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
//                .collect(
//                        Collectors.groupingBy(Dish::getDate, Collectors.summingInt(Dish::getCalories))
////                      Collectors.toMap(Meal::getDate, Meal::getCalories, Integer::sum)
//                );
//
//        return meals.stream()
//                .filter(filter)
//                .map(meal -> createTo(meal, caloriesSumByDate.get(meal.getDate()) > caloriesPerDay))
//                .collect(Collectors.toList());
//    }

//    public static MealTo createTo(Dish meal, boolean excess) {
//        return new MealTo(meal.getId(), meal.getDate(), meal.getDescription(), meal.getCalories(), excess);
//    }
}
