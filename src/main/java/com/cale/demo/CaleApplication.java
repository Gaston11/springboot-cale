package com.cale.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CaleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaleApplication.class, args);
    }
}