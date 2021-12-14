package ru.ustinov.voting.web.voting;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.model.Vote;
import ru.ustinov.voting.repository.VoteRepository;
import ru.ustinov.voting.service.VoteService;
import ru.ustinov.voting.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.TimeZone;

/**
 * //TODO add comments.
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 09.11.2021
 */
@Slf4j
public class AbstractVoteController {

    @Autowired
    private VoteService voteService;

    @Autowired
    private VoteRepository voteRepository;

    public Vote getVote(User user, LocalDate date) {
        log.info("get today's vote for user {}", user);
        Vote voteByUserAndDate = voteRepository.getVoteByUserAndDate(user, date);
        if (voteByUserAndDate == null) {
            voteByUserAndDate = new Vote(null, null, null);
        }
        return voteByUserAndDate;
    }

    public List<Vote> getVotes(User user) {
        log.info("get votes for user {}", user);
        return voteRepository.getVotesByUser(user);
    }

    public LocalTime getVotingTime(User user) {
        log.info("get voting time for user {}", user);
        return voteService.getVotingTime();
    }

    public void setTime(LocalTime time) {
        log.info("establish voting time to {}", time);
        voteService.setVotingTime(time);
    }

    public Vote vote(User user, int restaurant_id, TimeZone timeZone) {
        log.info("user {} is voting for restaurant {}", user.getName(), restaurant_id);
        return voteService.vote(user, restaurant_id, timeZone);
    }

}
