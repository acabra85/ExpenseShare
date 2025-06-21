// src/main/java/com/acabra/expenseshare/controller/ExpenseController.java
package com.acabra.expenseshare.controller;

import com.acabra.expenseshare.model.Expense;
import com.acabra.expenseshare.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing expenses.
 * Exposes endpoints for creating, retrieving, updating, and deleting expenses.
 */
@RestController // Marks this class as a REST controller, returning JSON/XML
@RequestMapping("/api/expenses") // Base path for all expense-related API endpoints
@CrossOrigin(origins = "http://localhost:3000") // Allow requests from your React frontend
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired // Injects ExpenseService
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Creates a new expense.
     * @param expense The Expense object to create.
     * @return ResponseEntity with the created Expense and HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
        Expense createdExpense = expenseService.createExpense(expense);
        return ResponseEntity.status(201).body(createdExpense);
    }

    /**
     * Retrieves all expenses.
     * @return ResponseEntity with a list of all Expenses and HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses() {
        List<Expense> expenses = expenseService.findAllExpenses();
        return ResponseEntity.ok(expenses);
    }

    /**
     * Retrieves an expense by its ID.
     * @param id The ID of the expense to retrieve.
     * @return ResponseEntity with the Expense if found (HTTP 200), or HTTP 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable String id) {
        Optional<Expense> expense = expenseService.findExpenseById(id);
        return expense.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Retrieves expenses for a specific group.
     * @param groupId The ID of the group.
     * @return ResponseEntity with a list of Expenses for the given group.
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Expense>> getExpensesByGroupId(@PathVariable String groupId) {
        List<Expense> expenses = expenseService.findExpensesByGroupId(groupId);
        return ResponseEntity.ok(expenses);
    }

    /**
     * Updates an existing expense.
     * @param id The ID of the expense to update.
     * @param expense The updated Expense object.
     * @return ResponseEntity with the updated Expense if found (HTTP 200), or HTTP 404 (Not Found).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable String id, @RequestBody Expense expense) {
        Expense updatedExpense = expenseService.updateExpense(id, expense);
        return updatedExpense != null ? ResponseEntity.ok(updatedExpense) : ResponseEntity.notFound().build();
    }

    /**
     * Deletes an expense by its ID.
     * @param id The ID of the expense to delete.
     * @return ResponseEntity with HTTP status 204 (No Content) if deleted, or HTTP 404 (Not Found).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable String id) {
        boolean deleted = expenseService.deleteExpense(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
    