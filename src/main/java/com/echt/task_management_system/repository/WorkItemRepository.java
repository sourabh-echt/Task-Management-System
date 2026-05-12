package com.echt.task_management_system.repository;

import com.echt.task_management_system.entity.WorkItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkItemRepository extends JpaRepository<WorkItem, UUID> {
    @EntityGraph(attributePaths = {"assignee", "reporter"})
    List<WorkItem> findAllByOrderByCreatedAtDesc();

      @Query("""
        SELECT DISTINCT w FROM WorkItem w
        LEFT JOIN FETCH w.project
        LEFT JOIN FETCH w.sprint
        LEFT JOIN FETCH w.parent
        LEFT JOIN FETCH w.team t
        LEFT JOIN FETCH t.teamMembers
        LEFT JOIN FETCH w.assignee
        LEFT JOIN FETCH w.reporter
        LEFT JOIN FETCH w.attachments
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

    List<WorkItem> findByWorkTypeOrderByCreatedAtDesc(WorkItem.WorkType workType);

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
