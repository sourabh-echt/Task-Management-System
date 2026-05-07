package com.echt.task_management_system.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.echt.task_management_system.entity.Project;

public interface ProjectRepository extends JpaRepository<Project,UUID>{

}
