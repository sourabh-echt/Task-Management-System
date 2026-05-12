package com.echt.task_management_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AttachmentResponse {
    private UUID id;
    private String filename;
    private String fileUrl;
    private Long fileSize;
}
