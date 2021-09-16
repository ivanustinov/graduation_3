package ru.javaops.topjava;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.javaops.topjava.model.Restaurant;
import ru.javaops.topjava.web.json.JsonUtil;

import java.util.List;

@SpringBootApplication
public class VotingManagerApplication {


    public static void main(String[] args) {
        SpringApplication.run(VotingManagerApplication.class, args);

    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            final List<Restaurant> restaurants = JsonUtil.readValues("""
                    [{\"name\":\"Харбин\",\"id\":1,\"dishes\":
                    [{\"id\":2,\"name\":\"Жаркое\",\"price\":500},{\"id\":3,\"name\":\"Компот\",\"price\":500},{\"id\":1,\"name\":\"Харчо\",\"price\":500}],\"votes\":[{\"id\":1,\"user\":null},{\"id\":2,\"user\":null}]},{\"name\":\"Си\",\"id\":2,\"dishes\":[{\"id\":5,
                    \"name\":\"Пиво\",\"price\":150},{\"id\":4,\"name\":\"Селедка\",\"price\":320}],\"votes\":[]}]""", Restaurant.class);
            System.out.println(restaurants);
        };
    }
}


