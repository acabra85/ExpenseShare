// src/main/java/com/acabra/expenseshare/service/security/UserDetailsServiceImpl.java
package com.acabra.expenseshare.service.security;

import com.acabra.expenseshare.model.UserAccess;
import com.acabra.expenseshare.repository.UserAccessRepository;
import org.springframework.security.core.userdetails.User; // Spring Security User
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * This service is responsible for loading user-specific data from the 'user_access' table
 * via the UserAccessRepository for authentication.
 */
@Service // Marks this class as a Spring service component
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAccessRepository userAccessRepository;

    @Autowired // Injects UserAccessRepository
    public UserDetailsServiceImpl(UserAccessRepository userAccessRepository) {
        this.userAccessRepository = userAccessRepository;
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may be case sensitive, or case insensitive depending on how the implementation
     * instance is configured.
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (an instance of UserDetails).
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     * GrantedAuthority.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve UserAccess entity from the database using the repository
        UserAccess userAccess = userAccessRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Convert the comma-separated roles string from UserAccess into a collection of GrantedAuthority objects
        Collection<? extends GrantedAuthority> authorities = userAccess.getRolesAsSet().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Spring Security roles convention (e.g., "ROLE_USER")
                .collect(Collectors.toList());

        // Build and return Spring Security's UserDetails object
        return User.builder()
                .username(userAccess.getUsername())
                .password(userAccess.getPassword()) // The password is already hashed from the DB
                .authorities(authorities)
                .build();
    }
}
