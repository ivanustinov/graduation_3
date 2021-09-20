package ru.javaops.topjava.web.voting;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.model.User;
import ru.javaops.topjava.repository.RestaurantRepository;
import ru.javaops.topjava.service.RestaurantService;
import ru.javaops.topjava.service.VoteService;
import ru.javaops.topjava.to.RestaurantTo;
import ru.javaops.topjava.web.AuthUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 03.09.2021
 */
@RestController
@RequestMapping(value = VotingController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class VotingController {
    static final String REST_URL = "/voting";

    private final RestaurantService restaurantService;
    private final VoteService voteService;

    @GetMapping
    public ResponseEntity<List<RestaurantTo>> getWithDishesToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get restaurants with dishes today for user {}",  authUser.id());
        return ResponseEntity.of(restaurantService.getWithDishes(LocalDate.now()));
    }

    @GetMapping("/results")
    public ResponseEntity<List<RestaurantTo>> getWithVotesAndDishesToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get restaurants with dishes today for user {}",  authUser.id());
        return ResponseEntity.of(restaurantService.getWithVotesAndDishes(LocalDate.now()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void vote(@AuthenticationPrincipal AuthUser authUser,
                     @RequestParam int restaurant_id,  @RequestParam LocalTime time) {
        final User user = authUser.getUser();
        log.info("user {} is voting for restaurant {}", user.getName(), restaurant_id);
        voteService.vote(user, restaurant_id, time);
    }
}
