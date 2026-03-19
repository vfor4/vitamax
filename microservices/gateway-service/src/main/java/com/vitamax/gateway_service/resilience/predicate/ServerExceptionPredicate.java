package com.vitamax.gateway_service.resilience.predicate;

import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerFilterFactory;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class ServerExceptionPredicate implements Predicate<Throwable> {
    @Override
    public boolean test(Throwable throwable) {
        if (throwable instanceof SpringCloudCircuitBreakerFilterFactory.CircuitBreakerStatusCodeException ex) {
            return ex.getStatusCode().is4xxClientError() || ex.getStatusCode().is5xxServerError();
        }
        return true;
    }
}