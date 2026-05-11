package com.echt.task_management_system.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.echt.task_management_system.entity.Sprint;

public interface SprintRepository extends JpaRepository<Sprint,UUID> {
      List<Sprint> findByProjectIdOrderByCreatedAtAsc(UUID projectId);
       Optional<Sprint> findByProjectIdAndStatus(UUID projectId, Sprint.SprintStatus status);
     
    @Query("""
        SELECT s FROM Sprint s
        LEFT JOIN FETCH s.workItems
        WHERE s.id = :sprintId
    """)
    Optional<Sprint> findByIdWithWorkItems(@Param("sprintId") UUID sprintId);
     boolean existsByProjectIdAndStatus(UUID projectId, Sprint.SprintStatus status);
     

}
