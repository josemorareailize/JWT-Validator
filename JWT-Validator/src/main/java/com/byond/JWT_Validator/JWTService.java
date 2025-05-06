package com.byond.JWT_Validator;

import com.auth0.jwk.JwkException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;

@Service
public class JWTService {
    public String verify(String authorizationToken) throws JwkException {
        if(authorizationToken == null || authorizationToken.isEmpty()){
            throw new JwkException("token is empty");
        }

        String[] tokenParts = authorizationToken.split(" ");

        if(tokenParts.length < 2){
            throw new JwkException("Invalid bearer token");
        }

        String token = tokenParts[1];
        DecodedJWT decodedJWT = JwtValidator.decodeToken(token);
        String expectedIssuer = decodedJWT.getIssuer();
        String kid = decodedJWT.getKeyId();

        RSAPublicKey publicKey = KeyUtil.getPublicKeyFromIssuer2(expectedIssuer, kid);
        JwtValidator validator = new JwtValidator(expectedIssuer, publicKey);


        DecodedJWT jwt = validator.validateToken(token);

        Claim usernameClaim = jwt.getClaim("preferred_username");
//        Claim usernameClaim = jwt.getClaim("gty");

        if(usernameClaim.isMissing()) {
            throw new JwkException("Username claim is null");
        }

        return usernameClaim.asString();

    }
}
