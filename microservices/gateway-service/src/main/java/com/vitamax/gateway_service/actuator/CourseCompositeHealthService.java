package com.vitamax.gateway_service.actuator;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.observability.DefaultSignalListener;
import reactor.core.observability.SignalListener;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class CourseCompositeHealthService {
    private static final Logger log = LoggerFactory.getLogger(CourseCompositeHealthService.class);
    private static final String ACTUATOR_HEALTH_ENDPOINT = "/actuator/health";
    private static final String STATUS = "status";

    private final WebClient webClient;

    public CourseCompositeHealthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<Health> getActuatorHealth(final String host) {
        return webClient.get().uri(host + ACTUATOR_HEALTH_ENDPOINT).retrieve()
                .bodyToMono(JsonNode.class)
                .timeout(Duration.ofSeconds(5))
                .map(this::toHealth)
                .tap(getSignalListenerSupplier(host))
                .onErrorResume(e -> Mono.just(Health.down(e).build()));
    }

    private static Supplier<SignalListener<Health>> getSignalListenerSupplier(final String host) {
        return () -> new DefaultSignalListener<>() {
            @Override
            public void doOnNext(Health health) {
                if (health.getStatus() != Status.UP) {
                    log.warn("Service [{}] is reporting status: {}", host, health.getStatus());
                }
            }

            @Override
            public void doOnError(Throwable e) {
                log.error("Service [{}] is DOWN — reason: {}", host, e.getMessage());
            }

            @Override
            public void doOnComplete() {
                log.debug("Health check for [{}] completed", host);
            }
        };
    }

    private Health toHealth(final JsonNode node) {
        return Optional.ofNullable(node.get(STATUS))
                .map(JsonNode::asText)
                .map(status -> Health.status(status).build())
                .orElse(Health.unknown().build());
    }

}
