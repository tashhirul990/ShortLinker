package com.shortLinker.ShortLinker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShortLinkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortLinkerApplication.class, args);
    }

}
