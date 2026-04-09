package com.vitamax.gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/openapi/**", "/oauth2/**", "/.well-known/**", "/token/**", "/login/**", "/actuator/**", "/eureka/**", "/default-ui.css", "/error").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/**").hasAuthority("SCOPE_api:read")
                        .pathMatchers(HttpMethod.POST, "/api/v1/**").hasAuthority("SCOPE_api:write")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/**").hasAuthority("SCOPE_api:write")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/**").hasAuthority("SCOPE_api:write")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}
