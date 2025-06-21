// src/main/java/com/acabra/expenseshare/service/ExpenseService.java
package com.acabra.expenseshare.service;

import com.acabra.expenseshare.model.Expense;
import com.acabra.expenseshare.repository.ExpenseRepository; // Assuming you'll create this repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for managing Expense entities.
 * Provides business logic for CRUD operations on expenses.
 */
@Service // Marks this class as a Spring service
@Transactional // Ensures methods are transactional (rollback on error)
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Autowired // Injects ExpenseRepository
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /**
     * Creates a new expense.
     * If the expense ID is not set, a new UUID is generated.
     * Sets the expense date.
     * @param expense The Expense object to create.
     * @return The created Expense.
     */
    public Expense createExpense(Expense expense) {
        if (expense.getId() == null || expense.getId().isEmpty()) {
            expense.setId(UUID.randomUUID().toString());
        }
        // Assuming date is set in Expense model's constructor or handled externally
        // expense.setDate(LocalDateTime.now());
        return expenseRepository.save(expense);
    }

    /**
     * Finds an expense by its ID.
     * @param id The ID of the expense.
     * @return An Optional containing the Expense if found.
     */
    public Optional<Expense> findExpenseById(String id) {
        return expenseRepository.findById(id);
    }

    /**
     * Finds all expenses.
     * @return A list of all Expenses.
     */
    public List<Expense> findAllExpenses() {
        return (List<Expense>) expenseRepository.findAll();
    }

    /**
     * Finds expenses by their associated group ID.
     * Requires a custom method in ExpenseRepository.
     * @param groupId The ID of the group.
     * @return A list of Expenses belonging to the specified group.
     */
    public List<Expense> findExpensesByGroupId(String groupId) {
        return expenseRepository.findByGroupId(groupId); // This method needs to be defined in ExpenseRepository
    }

    /**
     * Updates an existing expense.
     * @param id The ID of the expense to update.
     * @param updatedExpense The updated Expense object.
     * @return The updated Expense, or null if the original expense was not found.
     */
    public Expense updateExpense(String id, Expense updatedExpense) {
        return expenseRepository.findById(id).map(existingExpense -> {
            existingExpense.setDescription(updatedExpense.getDescription());
            existingExpense.setAmount(updatedExpense.getAmount());
            existingExpense.setPaidBy(updatedExpense.getPaidBy());
            existingExpense.setOwedBy(updatedExpense.getOwedBy());
            return expenseRepository.save(existingExpense);
        }).orElse(null);
    }

    /**
     * Deletes an expense by its ID.
     * @param id The ID of the expense to delete.
     * @return True if the expense was deleted, false otherwise.
     */
    public boolean deleteExpense(String id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
