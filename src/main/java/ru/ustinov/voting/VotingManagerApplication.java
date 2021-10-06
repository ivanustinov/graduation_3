package ru.ustinov.voting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.ustinov.voting.model.Restaurant;
import ru.ustinov.voting.service.IsolationLavel;

@SpringBootApplication
public class VotingManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VotingManagerApplication.class, args);
    }
}


