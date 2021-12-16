package ru.ustinov.voting.web.voting;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.web.AuthUser;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;


/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 25.10.2021
 */
@Hidden
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
    public Vote vote(@AuthenticationPrincipal AuthUser authUser, @RequestParam int restaurant_id) {
        final User user = authUser.getUser();
        return super.vote(user, restaurant_id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/time_zone")
    public void setTimezone(String timeZone) {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/time_zone")
    public Set<String> getTimezone() {
        return ZoneId.getAvailableZoneIds();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/current_time_zone")
    public TimeZone getCurrentTimezone() {
        return TimeZone.getDefault();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/set_time")
    public void setTime(@NonNull @RequestParam LocalTime time) {
        super.setTime(time);
    }
}
