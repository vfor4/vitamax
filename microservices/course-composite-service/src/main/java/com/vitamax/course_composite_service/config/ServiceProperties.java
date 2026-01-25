package com.vitamax.course_composite_service.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@ConfigurationProperties
@Component
@Getter
@Setter
@RequiredArgsConstructor
public final class ServiceProperties {
    private final Map<String, ServiceConfig> services;

    public record ServiceConfig(@NotBlank String host, @NotBlank String port) {
    }

    public String toUrl(final String serviceName) {
        return Optional.ofNullable(services.get(serviceName))//
                .map(sc -> "http://" + sc.host() + ":" + sc.port())//
                .orElseThrow(IllegalArgumentException::new);
    }
}
