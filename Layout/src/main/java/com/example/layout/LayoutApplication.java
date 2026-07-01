package com.example.layout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LayoutApplication {
    public static void main(String[] args) {
        SpringApplication.run(LayoutApplication.class, args);
    }
}
