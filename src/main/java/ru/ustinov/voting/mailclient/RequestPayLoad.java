package ru.ustinov.voting.mailclient;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.ustinov.voting.to.RestaurantTo;
import ru.ustinov.voting.to.UserTo;

import java.util.Set;

/**
 * //TODO add comments.
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 07.01.2024
 */
@Data
@AllArgsConstructor
public class RequestPayLoad {

    private RestaurantTo restaurantTo;

    private Set<UserTo> users;

}
