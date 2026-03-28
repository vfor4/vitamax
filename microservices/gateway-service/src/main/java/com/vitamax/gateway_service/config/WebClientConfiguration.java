package com.vitamax.gateway_service.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
public class WebClientConfiguration {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    SslContext sslContext(SslBundles sslBundles) {
        final var bundle = sslBundles.getBundle("gateway-bundle");
        final SslContext sslContext;

        try {
            sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(bundle.getManagers().getTrustManagerFactory())
                    .keyManager(bundle.getManagers().getKeyManagerFactory())
                    .build();
        } catch (SSLException e) {
            throw new IllegalStateException("Failed to create SSL context", e);
        }
        return sslContext;
    }

    @Bean
    WebClient webClient(WebClient.Builder webClientBuilder, SslContext sslContext) {
        return webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .secure(ssl -> ssl.sslContext(sslContext))
                ))
                .build();
    }
}
