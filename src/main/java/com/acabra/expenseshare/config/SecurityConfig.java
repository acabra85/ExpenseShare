package com.acabra.expenseshare.config;

import com.acabra.expenseshare.service.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Spring Security configuration for the ExpenseShare application.
 * This class sets up basic form-based authentication and URL authorization.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
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
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(authorize -> authorize
                        // Permit access to the login page and error pages directly
                        .requestMatchers("/login", "/error").permitAll()
                        // Permit access to H2 console (direct string path is now sufficient)
                        .requestMatchers("/h2-console/**").permitAll()
                        // Permit static resources like CSS, JS, images (if you had any)
                        // .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // Uncomment if you add static resources later
                        // Require authentication for all other requests
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        // RE-ENABLE CSRF and configure it for login form
        http.csrf(csrf -> csrf
                // Ignore CSRF for H2 console
                .ignoringRequestMatchers("/h2-console/**")
        );

        // Configure headers to allow H2 console to be displayed in an iframe
        http.headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );

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
}
