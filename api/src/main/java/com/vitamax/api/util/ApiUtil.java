package com.vitamax.api.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@UtilityClass
public class ApiUtil {
    public URI buildCreatedLocation(final UUID id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id.toString())
                .toUri();
    }

}
