package com.byond.JWT_Validator;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class KeyUtil {
    public static RSAPublicKey loadPublicKey(String certPath) throws Exception {
        try (InputStream in = KeyUtil.class.getClassLoader().getResourceAsStream(certPath)) {
            if (in == null) {
                throw new IllegalArgumentException("Certificate not found at path: " + certPath);
            }
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) factory.generateCertificate(in);
            PublicKey publicKey = certificate.getPublicKey();

            if (!(publicKey instanceof RSAPublicKey)) {
                throw new IllegalArgumentException("Public key is not an RSA public key.");
            }

            return (RSAPublicKey) publicKey;
        }
    }

    public static RSAPrivateKey loadPrivateKey(String filepath) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(filepath)))
                .replaceAll("-----\\w+ PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    public static String getIssuer(String token) {

        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            System.out.println("Invalid JWT token.");
            throw new RuntimeException("Invalid JWT token");
        }

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        JsonNode jsonNode;

        try {
            jsonNode = new ObjectMapper().readTree(payload);
        } catch (java.io.IOException e) {
            System.out.println("Invalid JWT token. " + e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
        return jsonNode.get("iss").asText();
    }

    public static RSAPublicKey getPublicKeyFromIssuer(String issuer,  String kid) throws Exception {
        // Step 1: Discover JWKS URI
        String discoveryUrl = issuer + "/.well-known/openid-configuration";
        JsonNode discoveryJson = new ObjectMapper().readTree(URI.create(discoveryUrl).toURL());
        String jwksUri = discoveryJson.get("jwks_uri").asText();

        // Step 2: Fetch JWKS
        JsonNode jwks = new ObjectMapper().readTree(URI.create(jwksUri).toURL());
        JsonNode jwk = null;
        for (JsonNode key : jwks.get("keys")) {
            if (key.get("kid").asText().equals(kid)) {
                jwk = key;
                break;
            }
        }

        if (jwk == null) {
            throw new RuntimeException("No matching key found in JWKS");
        }

        // Step 3: Convert to Public Key
        String n = jwk.get("n").asText(); // modulus
        String e = jwk.get("e").asText(); // exponent

        byte[] modulusBytes = Base64.getUrlDecoder().decode(n);
        byte[] exponentBytes = Base64.getUrlDecoder().decode(e);

        java.math.BigInteger modulus = new java.math.BigInteger(1, modulusBytes);
        java.math.BigInteger exponent = new java.math.BigInteger(1, exponentBytes);

        java.security.spec.RSAPublicKeySpec keySpec = new java.security.spec.RSAPublicKeySpec(modulus, exponent);
        RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);

        return publicKey;

    }

    public static RSAPublicKey getPublicKeyFromIssuer2(String issuer,  String kid) throws JwkException {
        // Build JWK provider for your Auth0 tenant
        JwkProvider provider = new JwkProviderBuilder(issuer)
                .cached(10, 24, TimeUnit.HOURS) // cache 10 keys for 24 hours
                .build();

        // Decode JWT to get the `kid` from the header

        // Get the public key from Auth0 JWKS endpoint using the `kid`
        Jwk jwk = provider.get(kid);
        RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();

        return publicKey;
    }

}
