// src/main/java/com/acabra/expenseshare/controller/GroupController.java
package com.acabra.expenseshare.controller;

import com.acabra.expenseshare.model.Group;
import com.acabra.expenseshare.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing groups.
 * Exposes endpoints for creating, retrieving, updating, and deleting groups.
 */
@RestController // Marks this class as a REST controller, returning JSON/XML
@RequestMapping("/api/groups") // Base path for all group-related API endpoints
@CrossOrigin(origins = "http://localhost:3000") // Allow requests from your React frontend
public class GroupController {

    private final GroupService groupService;

    @Autowired // Injects GroupService
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Creates a new group.
     * @param group The Group object to create.
     * @return ResponseEntity with the created Group and HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        Group createdGroup = groupService.createGroup(group);
        return ResponseEntity.status(201).body(createdGroup);
    }

    /**
     * Retrieves all groups.
     * @return ResponseEntity with a list of all Groups and HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        List<Group> groups = groupService.findAllGroups();
        return ResponseEntity.ok(groups);
    }

    /**
     * Retrieves a group by its ID.
     * @param id The ID of the group to retrieve.
     * @return ResponseEntity with the Group if found (HTTP 200), or HTTP 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroupById(@PathVariable String id) {
        Optional<Group> group = groupService.findGroupById(id);
        return group.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing group.
     * @param id The ID of the group to update.
     * @param group The updated Group object.
     * @return ResponseEntity with the updated Group if found (HTTP 200), or HTTP 404 (Not Found).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable String id, @RequestBody Group group) {
        Group updatedGroup = groupService.updateGroup(id, group);
        return updatedGroup != null ? ResponseEntity.ok(updatedGroup) : ResponseEntity.notFound().build();
    }

    /**
     * Deletes a group by its ID.
     * @param id The ID of the group to delete.
     * @return ResponseEntity with HTTP status 204 (No Content) if deleted, or HTTP 404 (Not Found).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable String id) {
        boolean deleted = groupService.deleteGroup(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
    