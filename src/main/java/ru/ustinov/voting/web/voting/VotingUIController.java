package ru.ustinov.voting.web.voting;

import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.web.AuthUser;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.TimeZone;


/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 25.10.2021
 */
@ApiIgnore
@RestController
@RequestMapping(value = VotingUIController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class VotingUIController extends AbstractVoteController {

    public static final String REST_URL = "/voting";

    @GetMapping
    public Vote getVote(@AuthenticationPrincipal AuthUser authUser) {
        return super.getVote(authUser.getUser(), LocalDate.now());
    }

    @GetMapping("/my_votes")
    public List<Vote> getVotes(@AuthenticationPrincipal AuthUser authUser) {
        return super.getVotes(authUser.getUser());
    }

    @GetMapping("/voting_time")
    public LocalTime getVotingTime(@AuthenticationPrincipal AuthUser authUser) {
        return super.getVotingTime(authUser.getUser());
    }

    @PostMapping
    public Vote vote(@AuthenticationPrincipal AuthUser authUser, @RequestParam int restaurant_id, TimeZone timeZone) {
        final User user = authUser.getUser();
        return super.vote(user, restaurant_id, timeZone);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/set_time")
    public void setTime(@NonNull @RequestParam LocalTime time) {
        super.setTime(time);
    }
}
