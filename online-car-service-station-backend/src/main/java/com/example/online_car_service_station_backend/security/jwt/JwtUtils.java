package com.example.online_car_service_station_backend.security.jwt;

import com.example.online_car_service_station_backend.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// Justification: This utility class is responsible for generating JWT tokens (after successful authentication)
// and validating incoming JWT tokens from client requests. It encapsulates JWT-specific logic.
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Justification: @Value injects properties from application.properties.
    // jwtSecret is the secret key used for signing and verifying JWTs. It must be kept secure.
    // jwtExpirationMs defines the token's validity duration in milliseconds.
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // Justification: Helper method to generate a signing key from the secret string.
    // Keys.hmacShaKeyFor is recommended for HMAC-SHA algorithms as it ensures strong keys.
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Justification: Generates a JWT token after a user successfully authenticates.
    // It includes the username as the subject and sets expiration time.
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // User's username as the subject
                .setIssuedAt(new Date()) // Token creation time
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Token expiration time
                .signWith(key(), SignatureAlgorithm.HS256) // Sign the token with our secret key and algorithm
                .compact(); // Builds the JWT string
    }

    // Justification: Extracts the username (subject) from a given JWT token.
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // Justification: Validates a given JWT token. It checks for various issues like
    // incorrect signature, expiration, malformation, or empty claims.
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}