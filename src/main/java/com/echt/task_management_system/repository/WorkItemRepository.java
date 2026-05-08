package com.echt.task_management_system.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.echt.task_management_system.entity.WorkItem;

@Repository
public interface WorkItemRepository extends JpaRepository<WorkItem, UUID> {

     @Query("""
        SELECT DISTINCT w FROM WorkItem w
        LEFT JOIN FETCH w.project
        LEFT JOIN FETCH w.sprint
        LEFT JOIN FETCH w.assignee
        LEFT JOIN FETCH w.reporter
        WHERE w.id = :workItemId
    """)
    Optional<WorkItem> findByIdWithDetails(
            @Param("workItemId") UUID workItemId
    );

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

    List<WorkItem> findBySprintId(UUID sprintId);

    List<WorkItem> findByProjectId(UUID projectId);

    boolean existsByItemKey(String itemKey);

    @Query("""
    SELECT w FROM WorkItem w
    LEFT JOIN FETCH w.assignee
    LEFT JOIN FETCH w.sprint
    WHERE w.project.id = :projectId
    ORDER BY w.createdAt ASC
    """)
    List<WorkItem> findBoardItems(
        @Param("projectId") UUID projectId
    );
}
