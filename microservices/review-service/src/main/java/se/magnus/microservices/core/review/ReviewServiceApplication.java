package se.magnus.microservices.core.review;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@ComponentScan("se.magnus")
@SpringBootApplication
public class ReviewServiceApplication {

    @Value("${app.threadPoolSize:10}") int poolSize;
    @Value("${app.taskQueueSize:100}") int queueSize;

    public static void main(String[] args) {
        SpringApplication.run(ReviewServiceApplication.class, args);
    }

    @Bean
    Scheduler jdbcScheduler(){
        return Schedulers.newBoundedElastic(poolSize, queueSize, "jdbc-pool");
    }
}
