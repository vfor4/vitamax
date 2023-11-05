package se.magnus.microservices.core.product.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.product.ProductService;
import se.magnus.api.event.Event;
import se.magnus.api.exceptions.EventProcessingException;

import java.util.function.Consumer;

@Configuration
public class MessageConsumerConfig {
    private static final Logger log = LoggerFactory.getLogger(MessageConsumerConfig.class);
    private final ProductService service;

    @Autowired
    public MessageConsumerConfig(final ProductService service) {
        this.service = service;
    }

    @Bean
    Consumer<Event<Integer, Product>> eventConsumer() {
        return event -> {
            switch (event.getType()) {
                case CREATE -> {
                    log.info("Create product with key: {}", event.getKey());
                    this.service.createProduct(event.getData()).block();
                }
                case DELETE -> {
                    log.info("Delete product with key: {}", event.getKey());
                    this.service.deleteProduct(event.getKey()).block();
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
