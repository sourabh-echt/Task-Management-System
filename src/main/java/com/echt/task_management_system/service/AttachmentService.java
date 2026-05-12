package com.echt.task_management_system.service;

import com.echt.task_management_system.dto.response.AttachmentResponse;
import com.echt.task_management_system.entity.Attachment;
import com.echt.task_management_system.entity.WorkItem;
import com.echt.task_management_system.repository.AttachmentRepository;
import com.echt.task_management_system.repository.WorkItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/jpeg"
    );

    private final AttachmentRepository attachmentRepository;
    private final WorkItemRepository workItemRepository;

    @Transactional
    public AttachmentResponse upload(UUID workItemId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Attachment file is required.");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Only pdf, png, jpg, and jpeg files are supported.");
        }

        WorkItem workItem = workItemRepository.findById(workItemId)
                .orElseThrow(() -> new EntityNotFoundException("Work item not found: " + workItemId));

        try {
            Path uploadDir = Path.of("uploads", "work-items", workItemId.toString());
            Files.createDirectories(uploadDir);

            String originalName = file.getOriginalFilename() == null ? "attachment" : file.getOriginalFilename();
            String safeName = Path.of(originalName).getFileName().toString().replaceAll("[^A-Za-z0-9._-]", "_");
            String storedName = UUID.randomUUID() + "_" + safeName;
            Path storedPath = uploadDir.resolve(storedName);
            file.transferTo(storedPath);

            Attachment attachment = Attachment.builder()
                    .workItem(workItem)
                    .filename(originalName)
                    .fileUrl(storedPath.toString().replace('\\', '/'))
                    .fileSize(file.getSize())
                    .build();

            Attachment saved = attachmentRepository.save(attachment);
            return AttachmentResponse.builder()
                    .id(saved.getId())
                    .filename(saved.getFilename())
                    .fileUrl(saved.getFileUrl())
                    .fileSize(saved.getFileSize())
                    .build();
        } catch (IOException ex) {
            throw new IllegalStateException("Could not store attachment.", ex);
        }
    }
}
