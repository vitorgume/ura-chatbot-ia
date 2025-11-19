package com.guminteligencia.ura_chatbot_ia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Bean(name = "defaultTaskScheduler")
    public TaskScheduler defaultTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2); // por ex: só para jobs “leves”
        scheduler.setThreadNamePrefix("default-scheduler-");
        return scheduler;
    }

    @Bean(name = "filaTaskScheduler")
    public TaskScheduler filaTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(3); // aqui você limita o consumo da fila
        scheduler.setThreadNamePrefix("fila-scheduler-");
        return scheduler;
    }
}

