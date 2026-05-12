package com.echt.task_management_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TeamResponse {
    private UUID teamId;
    private String teamName;
    private List<Member> teamMembers;

    public record Member(UUID id, String fullName, String email) {
    }
}
