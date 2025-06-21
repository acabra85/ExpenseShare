package com.acabra.expenseshare.filter;

import com.acabra.expenseshare.service.security.UserDetailsServiceImpl;
import com.acabra.expenseshare.util.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom filter for JWT authentication. This filter intercepts incoming requests,
 * extracts the JWT from the Authorization header, validates it using JwtUtil,
 * and sets the authenticated user in Spring Security's SecurityContextHolder.
 * It extends OncePerRequestFilter to ensure it's executed only once per request.
 */
@Component // Marks this as a Spring component for auto-detection and injection
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    @Autowired // Injects UserDetailsServiceImpl and JwtUtil
    public JwtRequestFilter(UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Get the Authorization header from the request
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Check if the Authorization header exists and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Extract the token (remove "Bearer ")
            try {
                username = jwtUtil.extractUsername(jwt); // Extract username from token
            } catch (Exception e) {
                // Log or handle invalid token exception (e.g., malformed, expired)
                logger.warn("Invalid JWT token: {}", e);
                // Continue filter chain but don't set authentication, leading to 401/403 if endpoint is secured
            }
        }

        // If username is extracted and no authentication is currently set in the SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load UserDetails from your UserDetailsService using the extracted username
            UserDetails userDetails = null;
            try {
                userDetails = this.userDetailsService.loadUserByUsername(username);
            } catch (Exception e) {
                logger.warn("User details not found for username extracted from JWT: {}", e);
                // Continue filter chain without authentication
            }

            // If userDetails are found and the token is valid for this user
            if (userDetails != null && jwtUtil.validateToken(jwt, userDetails)) {
                // Create an authentication token
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Set details from the HTTP request (e.g., remote address)
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication object in the SecurityContextHolder
                // This marks the user as authenticated for the current request
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // Continue the filter chain to the next filter or the target servlet/controller
        filterChain.doFilter(request, response);
    }
}
