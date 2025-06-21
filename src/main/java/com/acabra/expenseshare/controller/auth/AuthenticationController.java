package com.acabra.expenseshare.controller.auth;

import com.acabra.expenseshare.util.security.JwtUtil;
import com.acabra.expenseshare.service.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for user authentication using JWT.
 * It handles login requests, authenticates users, and issues JWTs.
 */
@RestController // Marks this class as a REST controller
@RequestMapping("/api/auth") // Base path for authentication endpoints
@CrossOrigin(origins = "http://localhost:3000") // IMPORTANT: Allow requests from your React frontend's origin
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    @Autowired // Inject dependencies
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    UserDetailsServiceImpl userDetailsService,
                                    JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Handles user login requests.
     * Authenticates the user with provided credentials and returns a JWT upon success.
     *
     * @param authenticationRequest A Map containing "username" and "password".
     * @return ResponseEntity with a JWT token or an error message.
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody Map<String, String> authenticationRequest) {
        String username = authenticationRequest.get("username");
        String password = authenticationRequest.get("password");

        if (username == null || password == null) {
            logger.error("Login attempt with missing username or password.");
            return ResponseEntity.badRequest().body(Map.of("message", "Username and password are required"));
        }

        try {
            // Authenticate the user using Spring Security's AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (BadCredentialsException e) {
            logger.warn("Invalid username or password for user: {}", username);
            return ResponseEntity.status(401).body(Map.of("message", "Invalid username or password"));
        } catch (Exception e) {
            logger.error("An error occurred during authentication for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(500).body(Map.of("message", "Authentication error: " + e.getMessage()));
        }

        // If authentication is successful, load UserDetails and generate JWT
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String jwt = jwtUtil.generateToken(userDetails);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        response.put("message", "Authentication successful");

        logger.info("User {} successfully authenticated and JWT issued.", username);
        return ResponseEntity.ok(response);
    }
}
