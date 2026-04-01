package com.vitamax.review_service.health;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class R2dbcHealthIndicator implements ReactiveHealthIndicator {

    private final ConnectionFactory connectionFactory;

    public R2dbcHealthIndicator(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Mono<Health> health() {
        return Mono.from(connectionFactory.create())
                .flatMap(conn -> Mono.from(conn.close()).thenReturn(Health.up().build()))
                .onErrorResume(e -> Mono.just(Health.down(e).build()));
    }
}
