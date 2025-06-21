// src/main/java/com/acabra/expenseshare/model/UserAccess.java
package com.acabra.expenseshare.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a user's access credentials and roles, mapping to the 'user_access' table.
 * This entity is used by Spring Data JDBC for persistence.
 */
@Table("USER_ACCESS") // Maps this entity to the 'user_access' table in the database
public class UserAccess {

    @Id // Marks 'id' as the primary key
    private Long id;
    private String username;
    private String password; // Storing the hashed password
    private String roles;    // Storing roles as a comma-separated string (e.g., "USER,ADMIN")

    // Constructors
    public UserAccess() {
    }

    public UserAccess(String username, String password, String roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    /**
     * Converts the comma-separated roles string into a collection of individual role strings.
     * @return A Set of role strings.
     */
    public Set<String> getRolesAsSet() {
        if (this.roles == null || this.roles.trim().isEmpty()) {
            return Set.of();
        }
        return Arrays.stream(this.roles.split(","))
                .map(String::trim)
                .map(String::toUpperCase) // Ensure roles are uppercase for consistency (e.g., "ROLE_USER")
                .collect(Collectors.toSet());
    }

    /**
     * Sets roles from a collection of strings, converting them into a comma-separated string.
     * @param roles A collection of role strings.
     */
    public void setRolesFromCollection(Collection<String> roles) {
        if (roles == null || roles.isEmpty()) {
            this.roles = "";
        } else {
            this.roles = roles.stream()
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .collect(Collectors.joining(","));
        }
    }
}
