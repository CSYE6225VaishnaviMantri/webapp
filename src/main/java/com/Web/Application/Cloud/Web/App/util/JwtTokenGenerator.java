package com.Web.Application.Cloud.Web.App.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenGenerator {


    private static final String SECRET_KEY = "JWT_TOKEN_GENERATOR"; // Replace with your secret key
    private static final long EXPIRATION_TIME_MS = 3600000; // Token expiration time in milliseconds (1 hour)

    public static String generateJwtToken(String username, UUID ID) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", ID); // Include user ID in the claims
        claims.put("username", username); // Include username in the claims

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    public static Claims decodeJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
