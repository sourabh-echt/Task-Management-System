package com.echt.task_management_system.repository;

import com.echt.task_management_system.entity.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    @EntityGraph(attributePaths = "teamMembers")
    List<Team> findAllByOrderByTeamNameAsc();
}
