package com.vitamax.auth_service.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    @Bean
    @Order(1)
    public org.springframework.security.web.SecurityFilterChain authorizationServerSecurityFilterChain(
            final HttpSecurity http
    ) throws Exception {
        final OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();
        http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .httpBasic(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @Order(2)
    public org.springframework.security.web.SecurityFilterChain applicationSecurityFilterChain(
            final HttpSecurity http
    ) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health/**", "/error").permitAll()
                        .anyRequest().denyAll()
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());
        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(
            final AuthClientProperties authClientProperties,
            final PasswordEncoder passwordEncoder
    ) {
        final List<RegisteredClient> clients = authClientProperties.clients()
                .stream()
                .map(client -> RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId(client.clientId())
                        .clientSecret(passwordEncoder.encode(client.clientSecret()))
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .tokenSettings(TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofHours(1))
                                .build())
                        .scopes(scopes -> scopes.addAll(client.scopes()))
                        .build())
                .toList();
        return new InMemoryRegisteredClientRepository(clients);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(
            final AuthClientProperties authClientProperties,
            final PemKeyLoader pemKeyLoader
    ) {
        final RSAPrivateKey privateKey = pemKeyLoader.loadPrivateKey(authClientProperties.privateKeyLocation());
        final RSAPublicKey publicKey = pemKeyLoader.loadPublicKey(authClientProperties.publicKeyLocation());
        final RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(authClientProperties.keyId())
                .build();
        final JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }
}
