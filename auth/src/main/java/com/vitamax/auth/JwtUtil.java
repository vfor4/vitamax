package com.vitamax.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateServiceToken(String serviceId, List<String> scopes) {
        return Jwts.builder()
                .subject(serviceId)
                .claim("scopes", scopes)
                .claim("type", "service")
                .issuer("auth-service")
                .audience().add("internal").and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .requireIssuer("auth-service")
                .requireAudience("internal")
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}