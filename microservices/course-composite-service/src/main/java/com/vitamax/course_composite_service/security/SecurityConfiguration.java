package com.vitamax.course_composite_service.security;

import com.vitamax.auth_service.ServiceAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final ServiceAuthFilter serviceAuthFilter;

    @Bean
    public SecurityWebFilterChain filterChain(final ServerHttpSecurity http) {
        return http
                .csrf(CsrfSpec::disable)
                .httpBasic(HttpBasicSpec::disable)
                .formLogin(FormLoginSpec::disable)
                .authorizeExchange(auth -> auth
                    .pathMatchers("/actuator/health").permitAll()
                    .pathMatchers("/swagger-ui.html").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/v1/course/**").hasAuthority("course:read")
                    .pathMatchers("/api/v1/course/**").hasAuthority("course:write")
                    .pathMatchers(HttpMethod.GET,"/api/v1/recommendation/**").hasAuthority("recommendation:read")
                    .pathMatchers("/api/v1/recommendation/**").hasAuthority("recommendation:write")
                    .pathMatchers(HttpMethod.GET,"/api/v1/review/**").hasAuthority("review:read")
                    .pathMatchers("/api/v1/review/**").hasAuthority("review:write")
                    .anyExchange().authenticated()
                )
                .addFilterBefore(serviceAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}