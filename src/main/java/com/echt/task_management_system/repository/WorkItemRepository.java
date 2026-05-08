package com.echt.task_management_system.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.echt.task_management_system.entity.WorkItem;

@Repository
public interface WorkItemRepository extends JpaRepository<WorkItem, UUID> {

     @Query("""
        SELECT w FROM WorkItem w
        LEFT JOIN FETCH w.assignee
        WHERE w.project.id = :projectId
        AND w.sprint IS NULL
        ORDER BY w.createdAt ASC
    """)
    List<WorkItem> findBacklogItems(
            @Param("projectId") UUID projectId
    );
}
