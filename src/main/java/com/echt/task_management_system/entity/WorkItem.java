package com.echt.task_management_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.micrometer.common.lang.Nullable;

/**
 * WorkItem entity — belongs to Sourabh's Create-Task Module.
 *
 * Represents a Task / Story / Bug / Epic.
 * Maps to the `work_items` table.
 */
@Entity
@Table(name = "work_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /** Null when item lives in the backlog. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @Column(name = "item_key", nullable = false, unique = true, length = 20)
    private String itemKey;   // e.g. SCRUM-9

    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", nullable = false, length = 20)
    private WorkType workType;

    @Column(nullable = false, length = 255)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkItemStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @Column(name = "story_points")
    private Integer storyPoints;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @OneToMany(mappedBy = "workItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "workItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Attachment> attachments = new ArrayList<>();

    // ── Enums ────────────────────────────────────────────────────────────────

    public enum WorkType {
        TASK, STORY, BUG, EPIC, SUBTASK
    }

    public enum WorkItemStatus {
        TO_DO, IN_PROGRESS, IN_REVIEW, DONE
    }

    public enum Priority {
        LOWEST, LOW, MEDIUM, HIGH, HIGHEST
    }
}
