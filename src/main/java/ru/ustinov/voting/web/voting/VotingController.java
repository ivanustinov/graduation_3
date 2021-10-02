package ru.ustinov.voting.web.voting;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.repository.VoteRepository;
import ru.ustinov.voting.service.RestaurantService;
import ru.ustinov.voting.service.VoteService;
import ru.ustinov.voting.to.RestaurantTo;
import ru.ustinov.voting.web.AuthUser;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/**
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
    public static final String REST_URL = "/voting";

    private final RestaurantService restaurantService;
    private final VoteService voteService;
    private final VoteRepository voteRepository;

    @GetMapping
    @Cacheable(cacheNames = "restaurants", key = "#root.methodName")
    public List<Restaurant> getWithDishesToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get restaurants with dishes today for user {}", authUser.id());
        return restaurantService.getWithDishes(LocalDate.now());
    }

    @GetMapping("/my-vote")
    public Vote getVoteToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get today's vote for user {}", authUser.id());
        return voteRepository.getVoteByUserAndDate(authUser.getUser(), LocalDate.now());
    }

    @GetMapping("/my-votes")
    public List<Vote> getVotes(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get votes for user {}", authUser.id());
        return voteRepository.getVotesByUser(authUser.getUser());
    }

    @GetMapping("/results")
    @Cacheable(cacheNames = "votes", key = "#root.methodName")
    public List<RestaurantTo> getWithVotesAndDishesToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get restaurants with dishes today for user {}", authUser.id());
        return restaurantService.getWithVotesAndDishes(LocalDate.now());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Vote> vote(@AuthenticationPrincipal AuthUser authUser, @RequestParam int restaurant_id) {
        final User user = authUser.getUser();
        log.info("user {} is voting for restaurant {}", user.getName(), restaurant_id);
        final Vote vote = voteService.vote(user, restaurant_id);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(vote.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(vote);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void voteAnotherOne(@AuthenticationPrincipal AuthUser authUser, @RequestParam int restaurant_id) {
        final User user = authUser.getUser();
        log.info("user {} is voting another one for restaurant {}", user.getName(), restaurant_id);
        voteService.vote(user, restaurant_id);
    }
}
