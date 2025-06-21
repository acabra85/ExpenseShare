// src/main/java/com/acabra/expenseshare/repository/GroupRepository.java
package com.acabra.expenseshare.repository;

import com.acabra.expenseshare.model.Group;
import org.springframework.data.repository.CrudRepository; // Provides basic CRUD operations

import java.util.List; // For findAll which CrudRepository returns as Iterable

/**
 * Spring Data JDBC Repository for the Group entity.
 * Provides standard CRUD operations and can include custom query methods if needed.
 */
public interface GroupRepository extends CrudRepository<Group, String> {

    // CrudRepository already provides findById, save, findAll, deleteById, existsById
    // You can add custom query methods here if needed, e.g.,
    List<Group> findByCreatedBy(String userId);
}
