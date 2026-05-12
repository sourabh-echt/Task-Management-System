package com.echt.task_management_system.service;

import com.echt.task_management_system.dto.request.CreateTeamRequest;
import com.echt.task_management_system.dto.response.TeamResponse;
import com.echt.task_management_system.entity.Team;
import com.echt.task_management_system.entity.User;
import com.echt.task_management_system.repository.TeamRepository;
import com.echt.task_management_system.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Transactional
    public TeamResponse create(CreateTeamRequest request) {
        Set<User> members = request.getTeamMemberIds().stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("User not found: " + id)))
                .collect(Collectors.toSet());

        Team team = Team.builder()
                .teamName(request.getTeamName())
                .teamMembers(members)
                .build();

        return toResponse(teamRepository.save(team));
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getAll() {
        return teamRepository.findAllByOrderByTeamNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public TeamResponse toResponse(Team team) {
        if (team == null) {
            return null;
        }
        return TeamResponse.builder()
                .teamId(team.getId())
                .teamName(team.getTeamName())
                .teamMembers(team.getTeamMembers().stream()
                        .map(user -> new TeamResponse.Member(
                                user.getId(),
                                user.getDisplayName() != null && !user.getDisplayName().isBlank()
                                        ? user.getDisplayName()
                                        : user.getUsername(),
                                user.getEmail()
                        ))
                        .toList())
                .build();
    }
}
