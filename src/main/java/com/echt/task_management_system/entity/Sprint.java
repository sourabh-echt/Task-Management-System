package com.echt.task_management_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Sprint entity — belongs to Dhananjay's Sprint Module.
 *
 * Maps to the `sprints` table.
 */
@Entity
@Table(name = "sprints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sprint extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String goal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SprintStatus status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    /** Work items assigned to this sprint (backlog items have sprint = null). */
    @OneToMany(mappedBy = "sprint", fetch = FetchType.LAZY)
    @Builder.Default
    private List<WorkItem> workItems = new ArrayList<>();

    // ── Domain helpers ───────────────────────────────────────────────────────

    public void start(LocalDate start, LocalDate end) {
        if (this.status != SprintStatus.PLANNED) {
            throw new IllegalStateException("Only PLANNED sprints can be started.");
        }
        this.status    = SprintStatus.ACTIVE;
        this.startDate = start;
        this.endDate   = end;
    }

    public void complete() {
        if (this.status != SprintStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE sprints can be completed.");
        }
        this.status = SprintStatus.COMPLETED;
    }

    public enum SprintStatus {
        PLANNED, ACTIVE, COMPLETED
    }
}
