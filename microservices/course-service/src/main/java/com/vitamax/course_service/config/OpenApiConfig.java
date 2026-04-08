package com.vitamax.course_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
    security = @SecurityRequirement(name = "OAuth2")
)
@SecurityScheme(
    name = "OAuth2",
    type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
            authorizationCode = @OAuthFlow(
                authorizationUrl = "${app.auth.authorize-uri}",
                tokenUrl = "${app.auth.token-uri}",
                scopes = {
                    @OAuthScope(name = "api:read", description = "API read permission"),
                    @OAuthScope(name = "api:write", description = "API write permission")
                }
            )
        )
)
public class OpenApiConfig {
    // No @Bean needed — annotations do all the work
}