package com.vitamax.course_composite_service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CourseCompositeHealthService {

    private static final String ACTUATOR_HEALTH_ENDPOINT = "/actuator/health";
    private static final String STATUS = "status";

    private final WebClient webClient;

    public CourseCompositeHealthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<Health> getActuatorHealth(final String host) {
        return webClient.get().uri(host + ACTUATOR_HEALTH_ENDPOINT).retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> Health.status(node.get(STATUS).asText()).build())
                .onErrorResume(e -> Mono.just(Health.down(e).build()));
    }

}
