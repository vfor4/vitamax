package se.magnus.springcloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.logging.Level;

@Configuration
public class HealthCheckConfiguration {
    private static final Logger log = LoggerFactory.getLogger(GatewayApplication.class);

    private final WebClient webClient;

    public HealthCheckConfiguration(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    private Mono<Health> getHealth(String baseUrl) {
        final var url = baseUrl + "/actuator/health";
        log.debug("Call health check from: {}", url);
        return webClient.get().uri(url).retrieve()
                .bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
                .log(log.getName(), Level.FINE);
    }

    @Bean
    ReactiveHealthContributor healthContributor() {
        final var registry = new LinkedHashMap<String, ReactiveHealthIndicator>();
        registry.put("product", () -> getHealth("http://product"));
        registry.put("recommendation", () -> getHealth("http://recommendation"));
        registry.put("review", () -> getHealth("http://review"));
        registry.put("product-composite", () -> getHealth("http://product-composite"));
        return CompositeReactiveHealthContributor.fromMap(registry);
    }
}
