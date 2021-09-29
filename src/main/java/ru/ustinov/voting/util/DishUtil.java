package ru.ustinov.voting.util;

import lombok.experimental.UtilityClass;
import ru.ustinov.voting.model.Dish;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class DishUtil {


    public static Map<Integer, List<Dish>> groupByRestaurant(Collection<Dish> dishes) {
        return dishes.stream().collect(
                Collectors.groupingBy((Dish dish1) -> dish1.getRestaurant().getId(), Collectors.toList())
        );
    }

}
