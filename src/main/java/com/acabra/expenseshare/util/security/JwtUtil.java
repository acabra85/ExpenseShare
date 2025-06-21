// src/main/java/com/acabra/expenseshare/util/security/JwtUtil.java
package com.acabra.expenseshare.util.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm; // Keep this import for now if SignatureAlgorithm enum is still used elsewhere
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for generating, validating, and extracting information from JWTs.
 * Uses a secret key for signing tokens and Spring's @Value to inject properties.
 */
@Component
public class JwtUtil {

    // Secret key for JWT signing, loaded from application.properties
    // IMPORTANT: In a production environment, this key should be very strong
    // and ideally loaded from a secure external source (e.g., environment variable, Vault).
    @Value("${jwt.secret:defaultSecretKeyForDevelopmentOnlyPleaseChangeMeInProdDontUseThisInProduction}")
    private String secret;

    // Token expiration time in milliseconds (e.g., 24 hours)
    @Value("${jwt.expiration:86400000}") // Default to 24 hours in milliseconds
    private long expiration;

    /**
     * Retrieves the signing key from the base64 encoded secret.
     * @return The signing Key.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes); // Keys.hmacShaKeyFor creates a key for HS256 by default from the bytes
    }

    /**
     * Extracts a specific claim from the token using a claims resolver function.
     * @param token The JWT.
     * @param claimsResolver The function to resolve the desired claim.
     * @param <T> The type of the claim.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT.
     * @param token The JWT.
     * @return All claims as a Claims object.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    /**
     * Extracts the username (subject) from the token.
     * @param token The JWT.
     * @return The username.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the token.
     * @param token The JWT.
     * @return The expiration date.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Checks if the token is expired.
     * @param token The JWT.
     * @return True if the token is expired, false otherwise.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates the token against UserDetails and checks for expiration.
     * @param token The JWT.
     * @param userDetails The UserDetails to validate against.
     * @return True if the token is valid, false otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Generates a JWT for the given UserDetails.
     * @param userDetails The UserDetails for whom to generate the token.
     * @return The generated JWT string.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Add roles as a claim (e.g., "roles": ["ADMIN", "USER"])
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", "")) // Remove "ROLE_" prefix for cleaner roles
                .toList());
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates the JWT with claims, subject (username), issued date, expiration date, and signs it.
     * Uses the non-deprecated fluent API of JJWT.
     * @param claims Custom claims to include.
     * @param subject The subject of the token (username).
     * @return The signed JWT string.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Replaced setClaims()
                .setSubject(subject) // Replaced setSubject()
                .setIssuedAt(new Date(System.currentTimeMillis())) // Replaced setIssuedAt()
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Replaced setExpiration()
                .signWith(getSigningKey()) // Replaced signWith(key, SignatureAlgorithm)
                .compact();
    }

    public static void main(String[] args) {
        System.out.println(Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded()));
    }
}
