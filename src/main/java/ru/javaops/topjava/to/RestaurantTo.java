package ru.javaops.topjava.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.javaops.topjava.model.Dish;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.model.Vote;

import java.util.List;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 18.09.2021
 */

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RestaurantTo extends NamedTo {


    @ToString.Exclude
    @JsonIgnoreProperties({"restaurant", "date"})
    private List<Dish> dishes;


    @ToString.Exclude
    @JsonIgnoreProperties({"restaurant", "date"})
    private List<Vote> votes;

    public RestaurantTo(Restaurant restaurant) {
        super(restaurant.getId(), restaurant.getName());
    }

    public RestaurantTo(Restaurant restaurant, List<Dish> dishes) {
        super(restaurant.getId(), restaurant.getName());
        this.dishes = dishes;
    }

}
