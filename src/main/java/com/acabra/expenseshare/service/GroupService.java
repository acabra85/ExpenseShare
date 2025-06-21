// src/main/java/com/acabra/expenseshare/service/GroupService.java
package com.acabra.expenseshare.service;

import com.acabra.expenseshare.model.Group;
import com.acabra.expenseshare.repository.GroupRepository; // Assuming you'll create this repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // For transactional operations

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for managing Group entities.
 * Provides business logic for CRUD operations on groups.
 */
@Service // Marks this class as a Spring service
@Transactional // Ensures methods are transactional (rollback on error)
public class GroupService {

    private final GroupRepository groupRepository;

    @Autowired // Injects GroupRepository
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    /**
     * Creates a new group.
     * If the group ID is not set, a new UUID is generated.
     * Sets the creation timestamp and assigns the creator.
     * @param group The Group object to create.
     * @return The created Group.
     */
    public Group createGroup(Group group) {
        if (group.getId() == null || group.getId().isEmpty()) {
            group.setId(UUID.randomUUID().toString());
        }
        // Assuming createdAt and createdBy are set in Group model's constructor or handled externally
        // If not, uncomment/add lines like:
        // group.setCreatedAt(LocalDateTime.now());
        // group.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName()); // Example for actual user
        return groupRepository.save(group);
    }

    /**
     * Finds a group by its ID.
     * @param id The ID of the group.
     * @return An Optional containing the Group if found.
     */
    public Optional<Group> findGroupById(String id) {
        return groupRepository.findById(id);
    }

    /**
     * Finds all groups.
     * @return A list of all Groups.
     */
    public List<Group> findAllGroups() {
        // CrudRepository.findAll() returns Iterable, convert to List
        return (List<Group>) groupRepository.findAll();
    }

    /**
     * Updates an existing group.
     * @param id The ID of the group to update.
     * @param updatedGroup The updated Group object.
     * @return The updated Group, or null if the original group was not found.
     */
    public Group updateGroup(String id, Group updatedGroup) {
        return groupRepository.findById(id).map(existingGroup -> {
            existingGroup.setName(updatedGroup.getName());
            // Update members cautiously, typically you might have separate methods for adding/removing members
            existingGroup.setMembers(updatedGroup.getMembers());
            return groupRepository.save(existingGroup);
        }).orElse(null);
    }

    /**
     * Deletes a group by its ID.
     * @param id The ID of the group to delete.
     * @return True if the group was deleted, false otherwise.
     */
    public boolean deleteGroup(String id) {
        if (groupRepository.existsById(id)) {
            groupRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
