package com.vitamax.auth_service.config;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class PemKeyLoader {
    private final ResourceLoader resourceLoader;

    public PemKeyLoader(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public RSAPrivateKey loadPrivateKey(final String location) {
        try {
            final byte[] keyBytes = decodePem(readResource(location), "PRIVATE KEY");
            return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        } catch (Exception ex) {
            throw new IllegalStateException("failed to load private key from " + location, ex);
        }
    }

    public RSAPublicKey loadPublicKey(final String location) {
        try {
            final byte[] keyBytes = decodePem(readResource(location), "PUBLIC KEY");
            return (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (Exception ex) {
            throw new IllegalStateException("failed to load public key from " + location, ex);
        }
    }

    private String readResource(final String location) throws Exception {
        final Resource resource = resourceLoader.getResource(location);
        try (var inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private byte[] decodePem(final String pem, final String keyType) {
        final String normalized = pem
                .replace("-----BEGIN " + keyType + "-----", "")
                .replace("-----END " + keyType + "-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(normalized);
    }
}
