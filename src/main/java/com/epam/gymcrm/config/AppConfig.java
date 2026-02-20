package com.epam.gymcrm.config;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ComponentScan(basePackages = "com.epam.gymcrm")
@PropertySource("classpath:application.properties")

public class AppConfig {

    @Bean(name = "userStorage")
    public Map<UUID, User> userStorage(){
        return new ConcurrentHashMap<>();
    }

    @Bean(name = "trainerStorage")
    public Map<UUID, Trainer> trainerStorage(){
        return new ConcurrentHashMap<>();
    }

    @Bean(name = "traineeStorage")
    public Map<UUID, Trainee> traineeStorage(){
        return new ConcurrentHashMap<>();
    }

    @Bean(name = "trainingStorage")
    public Map<UUID, Training> trainingStorage(){
        return new ConcurrentHashMap<>();
    }

}