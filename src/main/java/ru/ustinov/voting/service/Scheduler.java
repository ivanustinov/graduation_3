package ru.ustinov.voting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import ru.ustinov.voting.to.RestaurantTo;
import ru.ustinov.voting.web.MyWebClient;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

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

    private final TaskScheduler taskScheduler;

    public Scheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @Scheduled(cron = "#{@scheduler.calculateCronExpression()}")
    public void sendEmails() {
        log.info("Start Sending Emails on time: " + LocalTime.now());
    }

    public String calculateCronExpression() {
        final LocalTime votingTime = voteService.getVotingTime();
        final int minute = votingTime.getMinute();
        final int hour = votingTime.getHour();
        final String cronExpression = "0 " + minute + " " + hour + " * * MON-FRI";
        log.info(cronExpression);
        return cronExpression;
    }

    public void updateVotingTimeOrTimeZone() {
        // Отменяем предыдущее выполнение
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);
        }
        String cronExpression = calculateCronExpression();
        // Пересоздаем задачу с новым cron-выражением
        scheduledFuture = taskScheduler.schedule(this::sendEmails, new CronTrigger(cronExpression, TimeZone.getDefault()));
    }

    class SendPostRequest implements Runnable {
        @Override
        public void run() {
            final RestaurantTo result = restaurantService.getResult(LocalDateTime.now());
            myWebClient.postRequest(result.getId());
        }
    }

}
