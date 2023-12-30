package ru.ustinov.voting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ustinov.voting.to.RestaurantTo;
import ru.ustinov.voting.web.MyWebClient;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * //TODO add comments.
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 14.12.2023
 */
@Service
@Slf4j
public class Scheduler {

    @Autowired
    private VoteService voteService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MyWebClient myWebClient;

    private ScheduledFuture<?> scheduledFuture;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final int secondsInDay = 24*60*60;

    @PostConstruct
    public void init() {
        // Рассчитываем время до следующего запуска
        long initialDelay = calculateInitialDelay(voteService.getVotingTime());
        scheduledFuture = scheduler.scheduleAtFixedRate(new SendPostRequest(), initialDelay, secondsInDay, TimeUnit.SECONDS);
    }

    private long calculateInitialDelay(LocalTime votingTime) {
        // Рассчитываем следующее время в секундах
        LocalDateTime targetDateTime = LocalDateTime.of(java.time.LocalDate.now(), votingTime);
        Duration duration = Duration.between(LocalDateTime.now(), targetDateTime);
        if (duration.isNegative() || duration.isZero()) {
            // Если текущее время уже прошло, добавляем 24 часа
            duration = duration.plusDays(1);
        }
        return duration.toSeconds();
    }

    class SendPostRequest implements Runnable {
        @Override
        public void run() {
            final RestaurantTo result = restaurantService.getResult(LocalDateTime.now());
            myWebClient.postRequest(result.getId());
        }
    }

    public void onChangeVotingTime() {
        log.info("change initial delay");
        scheduledFuture.cancel(false);
        final long initialDelay = calculateInitialDelay(voteService.getVotingTime());
        scheduledFuture = scheduler.scheduleAtFixedRate(new SendPostRequest(), initialDelay, secondsInDay, TimeUnit.SECONDS);
    }
}
