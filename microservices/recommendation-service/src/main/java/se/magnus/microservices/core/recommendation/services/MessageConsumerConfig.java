package se.magnus.microservices.core.recommendation.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.recommendation.RecommendationService;
import se.magnus.api.event.Event;
import se.magnus.api.exceptions.EventProcessingException;

import java.util.function.Consumer;

@Configuration
public class MessageConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumerConfig.class);
    private final RecommendationService service;

    public MessageConsumerConfig(final RecommendationService service) {
        this.service = service;
    }

    @Bean
    Consumer<Event<Integer, Recommendation>> eventConsumer() {
        return event -> {
            switch (event.getType()) {
                case CREATE -> {
                    log.info("Create recommendation with key: {}", event.getKey());
                    this.service.createRecommendation(event.getData()).block();
                }
                case DELETE -> {
                    log.info("delete recommendation with key: {}", event.getKey());
                    this.service.deleteRecommendations(event.getKey()).block();
                }
                default -> {
                    String errorMessage = "Incorrect event type: " + event.getType() + ", expected a CREATE or DELETE event";
                    log.warn(errorMessage);
                    throw new EventProcessingException(errorMessage);
                }
            }
            log.info("Message processing done!");
        };
    }
}
