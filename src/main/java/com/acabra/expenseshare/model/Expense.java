// src/main/java/com/acabra/expenseshare/model/Expense.java
package com.acabra.expenseshare.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Represents an expense within a group in the ExpenseShare application.
 * This entity is mapped to the 'expenses' table in the database.
 */
@Table("EXPENSES") // Ensure this matches your Liquibase tableName for expenses
public class Expense {

    @Id // Marks 'id' as the primary key
    private String id;
    private String groupId;
    private String description;
    private Double amount;
    private String paidBy; // User ID who paid
    private Map<String, Double> owedBy; // Map of User ID to amount owed by that user
    private LocalDateTime date;

    // Constructors
    public Expense() {
        this.id = UUID.randomUUID().toString(); // Generate ID automatically
        this.date = LocalDateTime.now(); // Set expense date
    }

    public Expense(String groupId, String description, Double amount, String paidBy, Map<String, Double> owedBy) {
        this(); // Call default constructor to initialize id and date
        this.groupId = groupId;
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
        this.owedBy = owedBy;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public Map<String, Double> getOwedBy() {
        return owedBy;
    }

    public void setOwedBy(Map<String, Double> owedBy) {
        this.owedBy = owedBy;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id='" + id + '\'' +
                ", groupId='" + groupId + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", paidBy='" + paidBy + '\'' +
                ", owedBy=" + owedBy +
                ", date=" + date +
                '}';
    }
}
