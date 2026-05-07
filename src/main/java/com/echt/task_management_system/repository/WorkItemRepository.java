package com.echt.task_management_system.repository;

import com.echt.task_management_system.entity.WorkItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkItemRepository extends JpaRepository<WorkItem, UUID> {
    boolean existsByItemKey(String itemKey);
}
