// src/main/java/com/acabra/expenseshare/repository/UserAccessRepository.java
package com.acabra.expenseshare.repository;

import com.acabra.expenseshare.model.UserAccess;
import org.springframework.data.repository.CrudRepository; // Using CrudRepository for basic CRUD

import java.util.Optional;

/**
 * Spring Data JDBC Repository for the UserAccess entity.
 * Provides standard CRUD operations and custom query methods for user access data.
 */
public interface UserAccessRepository extends CrudRepository<UserAccess, Long> {

    /**
     * Finds a UserAccess entity by its username.
     * Spring Data JDBC automatically generates the query for this method based on the method name.
     *
     * @param username The username to search for.
     * @return An Optional containing the UserAccess entity if found, or empty if not found.
     */
    Optional<UserAccess> findByUsername(String username);
}
