package ru.ustinov.voting.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.ustinov.voting.model.Dish;
import ru.ustinov.voting.model.Restaurant;

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
    private int countVotes;


    public RestaurantTo(Restaurant restaurant, int countVotes) {
        super(restaurant.getId(), restaurant.getName());
        this.dishes = restaurant.getDishes();
        this.countVotes = countVotes;
    }

}
