package com.vitamax.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ServiceTokenFilter {
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    @Value("${spring.application.name}")
    private String serviceId;

    public ExchangeFilterFunction authFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            final var token = jwtUtil.generateServiceToken(serviceId, jwtProperties.getDefaultScopes());

            final var authRequest = ClientRequest.from(request)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .build();

            return Mono.just(authRequest);
        });
    }
}