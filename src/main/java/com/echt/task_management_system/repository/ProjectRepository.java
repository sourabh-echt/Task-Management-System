package com.echt.task_management_system.repository;

import com.echt.task_management_system.entity.Project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository
        extends JpaRepository<Project, UUID> {

    /**
     * Get all projects sorted by name ascending
     */
    List<Project> findAllByOrderByNameAsc();

    /**
     * Check if a project key already exists
     * Example: TMS, CRM, DEV
     */
    boolean existsByKeyIgnoreCase(String key);
}