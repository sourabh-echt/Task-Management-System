package com.echt.task_management_system.service;

import com.echt.task_management_system.dto.CreateWorkItemRequest;
import com.echt.task_management_system.dto.CreateWorkItemResponse;
import com.echt.task_management_system.dto.DeleteWorkItemResponse;
import com.echt.task_management_system.dto.UpdateWorkItemRequest;
import com.echt.task_management_system.dto.UpdateWorkItemResponse;
import com.echt.task_management_system.entity.Project;
import com.echt.task_management_system.entity.User;
import com.echt.task_management_system.entity.WorkItem;
import com.echt.task_management_system.repository.ProjectRepository;
import com.echt.task_management_system.repository.UserRepository;
import com.echt.task_management_system.repository.WorkItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateWorkItemServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkItemRepository workItemRepository;

    @InjectMocks
    private CreateWorkItemService createWorkItemService;

    @Test
    void createStoresAssigneeAndReporterAndReturnsDisplayNames() {
        UUID projectId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();
        UUID reporterId = UUID.randomUUID();
        Project project = project("SCRUM");
        User assignee = user("assignee", "Assignee User");
        User reporter = user("reporter", "Reporter User");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));
        when(userRepository.findById(reporterId)).thenReturn(Optional.of(reporter));
        when(workItemRepository.existsByItemKey(any())).thenReturn(false);
        when(workItemRepository.save(any(WorkItem.class))).thenAnswer(invocation -> {
            WorkItem workItem = invocation.getArgument(0);
            workItem.setId(UUID.randomUUID());
            return workItem;
        });

        CreateWorkItemRequest request = new CreateWorkItemRequest(
                projectId,
                WorkItem.WorkType.EPIC,
                WorkItem.WorkItemStatus.TO_DO,
                "Login Module",
                assigneeId,
                reporterId
        );

        CreateWorkItemResponse response = createWorkItemService.create(request);

        assertThat(response.assignee()).isEqualTo("Assignee User");
        assertThat(response.reporter()).isEqualTo("Reporter User");

        ArgumentCaptor<WorkItem> workItemCaptor = ArgumentCaptor.forClass(WorkItem.class);
        verify(workItemRepository).save(workItemCaptor.capture());
        assertThat(workItemCaptor.getValue().getAssignee()).isSameAs(assignee);
        assertThat(workItemCaptor.getValue().getReporter()).isSameAs(reporter);
    }

    @Test
    void createKeepsAssigneeAndReporterUnassignedWhenIdsAreNotProvided() {
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project("SCRUM")));
        when(workItemRepository.existsByItemKey(any())).thenReturn(false);
        when(workItemRepository.save(any(WorkItem.class))).thenAnswer(invocation -> {
            WorkItem workItem = invocation.getArgument(0);
            workItem.setId(UUID.randomUUID());
            return workItem;
        });

        CreateWorkItemRequest request = new CreateWorkItemRequest(
                projectId,
                WorkItem.WorkType.TASK,
                WorkItem.WorkItemStatus.TO_DO,
                "Login Module",
                null,
                null
        );

        CreateWorkItemResponse response = createWorkItemService.create(request);

        assertThat(response.assignee()).isEqualTo("Unassigned");
        assertThat(response.reporter()).isEqualTo("Unassigned");
        verify(userRepository, never()).findById(any());
    }

    @Test
    void createFailsWhenAssigneeIdDoesNotExist() {
        UUID projectId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project("SCRUM")));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.empty());

        CreateWorkItemRequest request = new CreateWorkItemRequest(
                projectId,
                WorkItem.WorkType.BUG,
                WorkItem.WorkItemStatus.TO_DO,
                "Login Module",
                assigneeId,
                null
        );

        assertThatThrownBy(() -> createWorkItemService.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Assignee user not found");

        verify(workItemRepository, never()).save(any());
    }

    @Test
    void updateChangesOnlyProvidedStatus() {
        UUID workItemId = UUID.randomUUID();
        User existingAssignee = user("assignee", "Assignee User");
        WorkItem workItem = workItem(workItemId, existingAssignee);

        when(workItemRepository.findById(workItemId)).thenReturn(Optional.of(workItem));
        when(workItemRepository.save(any(WorkItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateWorkItemResponse response = createWorkItemService.update(
                workItemId,
                new UpdateWorkItemRequest("IN_PROGRESS", null, null)
        );

        assertThat(response.status()).isEqualTo(WorkItem.WorkItemStatus.IN_PROGRESS);
        assertThat(response.priority()).isEqualTo(WorkItem.Priority.MEDIUM);
        assertThat(response.assignee()).isEqualTo("Assignee User");
        assertThat(workItem.getAssignee()).isSameAs(existingAssignee);
        verify(userRepository, never()).findById(any());
        verify(workItemRepository).save(workItem);
    }

    @Test
    void updateChangesOnlyProvidedPriority() {
        UUID workItemId = UUID.randomUUID();
        WorkItem workItem = workItem(workItemId, null);

        when(workItemRepository.findById(workItemId)).thenReturn(Optional.of(workItem));
        when(workItemRepository.save(any(WorkItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateWorkItemResponse response = createWorkItemService.update(
                workItemId,
                new UpdateWorkItemRequest(null, "HIGH", null)
        );

        assertThat(response.status()).isEqualTo(WorkItem.WorkItemStatus.TO_DO);
        assertThat(response.priority()).isEqualTo(WorkItem.Priority.HIGH);
        assertThat(response.assignee()).isEqualTo("Unassigned");
    }

    @Test
    void updateChangesOnlyProvidedAssignee() {
        UUID workItemId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();
        WorkItem workItem = workItem(workItemId, null);
        User assignee = user("new-assignee", "New Assignee");

        when(workItemRepository.findById(workItemId)).thenReturn(Optional.of(workItem));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));
        when(workItemRepository.save(any(WorkItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateWorkItemResponse response = createWorkItemService.update(
                workItemId,
                new UpdateWorkItemRequest(null, null, assigneeId.toString())
        );

        assertThat(response.assignee()).isEqualTo("New Assignee");
        assertThat(workItem.getAssignee()).isSameAs(assignee);
    }

    @Test
    void updateChangesMultipleProvidedFields() {
        UUID workItemId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();
        WorkItem workItem = workItem(workItemId, null);
        User assignee = user("new-assignee", "New Assignee");

        when(workItemRepository.findById(workItemId)).thenReturn(Optional.of(workItem));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));
        when(workItemRepository.save(any(WorkItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateWorkItemResponse response = createWorkItemService.update(
                workItemId,
                new UpdateWorkItemRequest("IN_PROGRESS", "HIGH", assigneeId.toString())
        );

        assertThat(response.status()).isEqualTo(WorkItem.WorkItemStatus.IN_PROGRESS);
        assertThat(response.priority()).isEqualTo(WorkItem.Priority.HIGH);
        assertThat(response.assignee()).isEqualTo("New Assignee");
        assertThat(workItem.getStatus()).isEqualTo(WorkItem.WorkItemStatus.IN_PROGRESS);
        assertThat(workItem.getPriority()).isEqualTo(WorkItem.Priority.HIGH);
        assertThat(workItem.getAssignee()).isSameAs(assignee);
    }

    @Test
    void updateAllowsEmptyRequestAsNoOp() {
        UUID workItemId = UUID.randomUUID();
        WorkItem workItem = workItem(workItemId, null);

        when(workItemRepository.findById(workItemId)).thenReturn(Optional.of(workItem));
        when(workItemRepository.save(any(WorkItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateWorkItemResponse response = createWorkItemService.update(
                workItemId,
                new UpdateWorkItemRequest(null, null, null)
        );

        assertThat(response.status()).isEqualTo(WorkItem.WorkItemStatus.TO_DO);
        assertThat(response.priority()).isEqualTo(WorkItem.Priority.MEDIUM);
        assertThat(response.assignee()).isEqualTo("Unassigned");
        verify(userRepository, never()).findById(any());
    }

    @Test
    void updateFailsWhenAssigneeIdDoesNotExist() {
        UUID workItemId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();

        when(workItemRepository.findById(workItemId)).thenReturn(Optional.of(workItem(workItemId, null)));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createWorkItemService.update(
                workItemId,
                new UpdateWorkItemRequest(null, null, assigneeId.toString())
        ))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Assignee user not found");

        verify(workItemRepository, never()).save(any());
    }

    @Test
    void updateTreatsBlankFieldsAsNotProvided() {
        UUID workItemId = UUID.randomUUID();
        User existingAssignee = user("assignee", "Assignee User");
        WorkItem workItem = workItem(workItemId, existingAssignee);

        when(workItemRepository.findById(workItemId)).thenReturn(Optional.of(workItem));
        when(workItemRepository.save(any(WorkItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateWorkItemResponse response = createWorkItemService.update(
                workItemId,
                new UpdateWorkItemRequest("DONE", "", "")
        );

        assertThat(response.status()).isEqualTo(WorkItem.WorkItemStatus.DONE);
        assertThat(response.priority()).isEqualTo(WorkItem.Priority.MEDIUM);
        assertThat(response.assignee()).isEqualTo("Assignee User");
        assertThat(workItem.getAssignee()).isSameAs(existingAssignee);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void deleteRemovesExistingWorkItem() {
        UUID workItemId = UUID.randomUUID();
        WorkItem workItem = workItem(workItemId, null);

        when(workItemRepository.findById(workItemId)).thenReturn(Optional.of(workItem));

        DeleteWorkItemResponse response = createWorkItemService.delete(workItemId);

        assertThat(response.id()).isEqualTo(workItemId);
        assertThat(response.itemKey()).isEqualTo("SCRUM-123456");
        assertThat(response.message()).isEqualTo("Work item deleted successfully");
        verify(workItemRepository).delete(workItem);
    }

    @Test
    void deleteFailsWhenWorkItemDoesNotExist() {
        UUID workItemId = UUID.randomUUID();

        when(workItemRepository.findById(workItemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createWorkItemService.delete(workItemId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Work item not found");

        verify(workItemRepository, never()).delete(any());
    }

    private Project project(String key) {
        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setKey(key);
        project.setName("Scrum Project");
        return project;
    }

    private User user(String username, String displayName) {
        return User.builder()
                .username(username)
                .email(username + "@example.com")
                .password("password")
                .displayName(displayName)
                .build();
    }

    private WorkItem workItem(UUID workItemId, User assignee) {
        WorkItem workItem = WorkItem.builder()
                .project(project("SCRUM"))
                .itemKey("SCRUM-123456")
                .workType(WorkItem.WorkType.TASK)
                .summary("Login Module")
                .status(WorkItem.WorkItemStatus.TO_DO)
                .priority(WorkItem.Priority.MEDIUM)
                .assignee(assignee)
                .build();
        workItem.setId(workItemId);
        return workItem;
    }
}
