// src/main/java/com/acabra/expenseshare/repository/ExpenseRepository.java
package com.acabra.expenseshare.repository;

import com.acabra.expenseshare.model.Expense;
import org.springframework.data.repository.CrudRepository; // Provides basic CRUD operations

import java.util.List;

/**
 * Spring Data JDBC Repository for the Expense entity.
 * Provides standard CRUD operations and custom query methods for expense data.
 */
public interface ExpenseRepository extends CrudRepository<Expense, String> {

    /**
     * Finds all expenses associated with a specific group ID.
     * Spring Data JDBC automatically generates the query for this method.
     * @param groupId The ID of the group.
     * @return A list of Expenses belonging to the specified group.
     */
    List<Expense> findByGroupId(String groupId);
}
