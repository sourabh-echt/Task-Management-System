package com.echt.task_management_system.repository;

import com.echt.task_management_system.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findAllByOrderByNameAsc();

    boolean existsByKeyIgnoreCase(String key);
}
