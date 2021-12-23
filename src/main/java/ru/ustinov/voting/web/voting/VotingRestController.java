package ru.ustinov.voting.web.voting;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.web.AuthUser;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 03.09.2021
 */
@RestController
@RequestMapping(value = VotingRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Tag(name = "Voting Controller")
public class VotingRestController extends AbstractVoteController {

    public static final String REST_URL = "/rest/voting";

    @GetMapping
    public Vote getVote(@AuthenticationPrincipal @ApiIgnore AuthUser authUser) {
        return super.getVote(authUser.getUser(), LocalDate.now());
    }

    @GetMapping("/my-votes")
    public List<Vote> getVotes(@AuthenticationPrincipal @ApiIgnore AuthUser authUser) {
        return super.getVotes(authUser.getUser());
    }


    @ApiResponse(content = @Content(mediaType = "application/json",
                    schema = @Schema(format = "hh:mm:ss")))
    @GetMapping("/voting_time")
    public LocalTime getVotingTime(@AuthenticationPrincipal @ApiIgnore AuthUser authUser) {
        return super.getVotingTime(authUser.getUser());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/set_time")
    public void setTime(@NonNull @Parameter(schema = @Schema(format = "hh:mm"))  @RequestParam LocalTime time) {
        super.setTime(time);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponse(content = @Content(schema = @Schema(type = "string")))
    @GetMapping("/current_time_zone")
    public TimeZone getTimeZone() {
        return super.getTimeZone();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/time_zone")
    public void setTimeZone(@RequestParam String timeZone) {
        super.setTimeZone(timeZone);
    }

    @PostMapping
    public ResponseEntity<Vote> vote(@AuthenticationPrincipal @ApiIgnore AuthUser authUser, @RequestParam int restaurant_id) {
        final User user = authUser.getUser();
        final Vote vote = super.vote(user, restaurant_id);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(vote.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(vote);
    }
}
