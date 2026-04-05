package com.vitamax.auth_service.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "auth")
public record AuthClientProperties(
        @Valid @NotEmpty List<ClientProperties> clients,
        @NotBlank String keyId,
        @NotBlank String privateKeyLocation,
        @NotBlank String publicKeyLocation
) {

    public record ClientProperties(
            @NotBlank String clientId,
            @NotBlank String clientSecret,
            @NotEmpty List<String> scopes
    ) {
    }
}
