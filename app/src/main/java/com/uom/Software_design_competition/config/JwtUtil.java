package com.uom.Software_design_competition.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtUtil {

    private final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // runtime-initialized key — prefer a configurable secret (jwt.secret) for stable tokens
    private Key SECRET_KEY;

    @Value("${jwt.secret:}")
    private String configuredSecret;

    @PostConstruct
    public void init() {
        if (configuredSecret == null || configuredSecret.isBlank()) {
            // Fall back to generating a key but log a clear warning — ephemeral key will invalidate tokens after restart
            logger.warn("No jwt.secret configured — using a temporary key. Tokens will be invalid across restarts. Set the JWT_SECRET env var or jwt.secret property to a persistent value.");
            SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            // Use the provided secret bytes (must be long enough for HS256 — >= 32 bytes)
            SECRET_KEY = Keys.hmacShaKeyFor(configuredSecret.getBytes(StandardCharsets.UTF_8));
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
