package com.vitamax.api.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@UtilityClass
public class ApiUtil {
    public URI buildCreatedLocation(final String id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    public String extractIdFromHeader(final HttpHeaders headers) {
        final var path = headers.getLocation().getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }
}
