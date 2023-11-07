package se.magnus.microservices.composite.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.magnus.microservices.composite.product.services.ProductCompositeIntegration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class HealthCheckConfiguration {

    @Autowired
    private ProductCompositeIntegration integration;

    @Bean
    ReactiveHealthContributor getHealth() {
        final Map<String, ReactiveHealthIndicator> map = new HashMap<>();
        map.put("product", () -> integration.getProductHealth());
        map.put("recommendation", () -> integration.getRecommendationHealth());
        map.put("review", () -> integration.getReviewHealth());
        return CompositeReactiveHealthContributor.fromMap(map);
    }
}
