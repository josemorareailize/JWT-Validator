package com.byond.JWT_Validator;

import com.auth0.jwk.JwkException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class JWTController {

    private final JWTService jwtService;

    public JWTController(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/validate/**")
    public ResponseEntity<String> validateToken(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            String username = jwtService.verify(token);
            HttpHeaders headers = new HttpHeaders();
//            headers.set("x-trino-user", "ayexa");
            headers.set("x-trino-user", username);
            return new ResponseEntity<>(headers, HttpStatus.OK);
        }

        catch (JwkException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service is up and running");
    }
}

