package com.acabra.expenseshare.config;

import com.acabra.expenseshare.filter.JwtRequestFilter; // Import your JWT filter
import com.acabra.expenseshare.service.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Added for AuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Added for AuthenticationManager bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy; // For stateless session management
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // For adding JWT filter
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Spring Security configuration for the ExpenseShare application.
 * This class sets up JWT-based authentication and API authorization.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtRequestFilter jwtRequestFilter; // Inject your JWT filter

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Configures the security filter chain to define HTTP security rules.
     *
     * @param http HttpSecurity object to configure.
     * @return The configured SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for API-only backend (frontend handles its own CSRF if needed)
                .authorizeHttpRequests(authorize -> authorize
                        // Allow public access to the authentication endpoint
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll() // Assuming you'll add register later
                        // Allow OPTIONS requests for all /api/** paths (CORS preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll() // ADDED THIS LINE
                        // Allow access to H2 console
                        .requestMatchers("/h2-console/**").permitAll()
                        // All other API requests must be authenticated
                        .requestMatchers("/api/**").authenticated() // Protecting all /api endpoints
                        // All other non-API requests (e.g., /login, /, /error for existing Mustache) are still permitted or will be handled by stripping
                        .anyRequest().permitAll() // Allow other non-API paths for now, will strip later
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Set session management to stateless
                );

        // Add the JWT filter BEFORE Spring Security's UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // Configure headers to allow H2 console to be displayed in an iframe
        http.headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );

        // Disable form login and logout as we're using JWT for API
        http.formLogin(form -> form.disable());
        http.logout(logout -> logout.disable());


        return http.build();
    }

    /**
     * Provides a BCryptPasswordEncoder bean for encoding passwords.
     * This is the recommended way to securely store passwords.
     *
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager bean. This bean is used by the
     * AuthenticationController to perform authentication.
     *
     * @param authenticationConfiguration The AuthenticationConfiguration.
     * @return An AuthenticationManager instance.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures the DaoAuthenticationProvider to use our custom UserDetailsService
     * and PasswordEncoder.
     * (This is implicitly used by AuthenticationManager)
     *
     * @return A DaoAuthenticationProvider instance.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
