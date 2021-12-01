package ru.ustinov.voting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.web.restaurant.RestaurantRestController;
import ru.ustinov.voting.web.restaurant.RestaurantUIController;

@SpringBootApplication
public class VotingManagerApplication {

    @Autowired
    private Environment environment;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CacheManager cacheManager;

    public static void main(String[] args) {
        SpringApplication.run(VotingManagerApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//        return new CommandLineRunner() {
//            @Override
//            public void run(String... args) throws Exception {
//                final Object restaurantUIController = ctx.getBean("restaurantUIController");
//                if (restaurantUIController instanceof RestaurantUIController) {
//                    ((RestaurantUIController) restaurantUIController).delete(1);
//                }
//            }
//        };
//    }
}


