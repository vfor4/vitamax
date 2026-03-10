package com.vitamax.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceAuthFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        final var header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing token");
        }

        final var token = header.substring(7);

        try {
            final var claims = jwtUtil.validateToken(token);

            final var auth =
                new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    extractAuthorities(claims)
                );

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));

        } catch (ExpiredJwtException e) {
            return unauthorized(exchange, "Token expired");
        } catch (JwtException e) {
            return unauthorized(exchange, "Invalid token");
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        final var bytes = message.getBytes(StandardCharsets.UTF_8);
        final var buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
        final List<String> scopes = claims.get("scopes", List.class);
        return scopes == null ? List.of() :
               scopes.stream().map(SimpleGrantedAuthority::new).toList();
    }
}