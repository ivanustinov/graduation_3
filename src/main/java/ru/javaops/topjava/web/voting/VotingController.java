package ru.javaops.topjava.web.voting;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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
@Tag(name = "Voting Controller")
public class VotingController {
    static final String REST_URL = "/voting";

    private final RestaurantService restaurantService;
    private final VoteService voteService;

    @GetMapping
    @Cacheable(cacheNames = "res")
    public List<RestaurantTo> getWithDishesToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get restaurants with dishes today for user {}",  authUser.id());
        return restaurantService.getWithDishes(LocalDate.now());
    }

    @GetMapping("/results")
    @Cacheable(cacheNames = "votes")
    public List<RestaurantTo> getWithVotesAndDishesToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get restaurants with dishes today for user {}",  authUser.id());
        return restaurantService.getWithVotesAndDishes(LocalDate.now());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void vote(@AuthenticationPrincipal AuthUser authUser,
                     @RequestParam int restaurant_id,  @RequestParam @Parameter(schema = @Schema(type = "string")) LocalTime time) {
        final User user = authUser.getUser();
        log.info("user {} is voting for restaurant {}", user.getName(), restaurant_id);
        voteService.vote(user, restaurant_id, time);
    }
}
