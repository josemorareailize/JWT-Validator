package com.byond.JWT_Validator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

public class JwtValidator {

    private final RSAPublicKey publicKey;
    private JWTVerifier verifier;
//    private String certPath;
//    private String privateKeyPath;
    private String expectedIssuer;

//    public JwtValidator(String certPath, String privateKeyPath, String expectedIssuer) {
//        this.certPath = certPath;
//        this.privateKeyPath = privateKeyPath;
//        this.expectedIssuer = expectedIssuer;
//
//        try {
//            RSAPublicKey publicKey = KeyUtil.loadPublicKey(certPath);
//            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
//
//            verifier = JWT.require(algorithm)
//                    .withIssuer(expectedIssuer) // Optional
//                    .build();
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }

    public JwtValidator(String expectedIssuer, RSAPublicKey publicKey) {
        this.expectedIssuer = expectedIssuer;
        this.publicKey = publicKey;
        createVerifier();
    }

    public void createVerifier() {

        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);

            verifier = JWT.require(algorithm)
                    .withIssuer(expectedIssuer)
                    .build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static DecodedJWT decodeToken(String token) {
        return JWT.decode(token);
    }

    public DecodedJWT validateToken(String token) {

        return verifier.verify(token);
    }

    public String createToken(String privateKeyPath) {

        String token = null;
        try {
            RSAPrivateKey privateKey = KeyUtil.loadPrivateKey(privateKeyPath);

            token = JWT.create()
                    .withIssuer("your-issuer")
                    .withSubject("test-user")
                    .withClaim("role", "admin")
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1 hour
                    .sign(Algorithm.RSA256(null, privateKey));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return token;

    }
}
