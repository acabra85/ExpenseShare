package com.acabra.expenseshare.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Represents a group in the ExpenseShare application.
 * This entity is mapped to the 'groups' table in the database.
 */
@Table("GROUPS") // Ensure this matches your Liquibase tableName for groups
public class Group {

    @Id // Marks 'id' as the primary key
    private String id;
    private String name;
    private List<String> members; // Stores member IDs. Will be handled by JSON serialization/deserialization.
    private LocalDateTime createdAt;
    private String createdBy; // User ID of the creator

    // Constructors
    public Group() {
        this.id = UUID.randomUUID().toString(); // Generate ID automatically
        this.createdAt = LocalDateTime.now(); // Set creation timestamp
    }

    public Group(String name, List<String> members, String createdBy) {
        this(); // Call default constructor to initialize id and createdAt
        this.name = name;
        this.members = members;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", members=" + members +
                ", createdAt=" + createdAt +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
