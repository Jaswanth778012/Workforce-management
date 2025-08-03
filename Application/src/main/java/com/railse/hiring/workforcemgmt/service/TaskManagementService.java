package com.railse.hiring.workforcemgmt.service;

import java.util.List;

import com.railse.hiring.worforcemgmt.common.model.enums.Priority;
import com.railse.hiring.worforcemgmt.common.model.enums.ReferenceType;
import com.railse.hiring.workforcemgmt.dto.AssignByRefferenceRequest;
import com.railse.hiring.workforcemgmt.dto.TaskCreateRequest;
import com.railse.hiring.workforcemgmt.dto.TaskFetchByDataRequest;
import com.railse.hiring.workforcemgmt.dto.TaskManagementDto;
import com.railse.hiring.workforcemgmt.dto.TaskPriorityUpdateRequest;
import com.railse.hiring.workforcemgmt.dto.UpdateTaskRequest;

public interface TaskManagementService {
	  List<TaskManagementDto> createTasks(TaskCreateRequest request);
	   List<TaskManagementDto> updateTasks(UpdateTaskRequest request);
	   String assignByReference(AssignByRefferenceRequest request);
	   List<TaskManagementDto> fetchTasksByDate(TaskFetchByDataRequest request);
	   TaskManagementDto findTaskById(Long id);
	   List<TaskManagementDto> findAllTasks();
	   List<TaskManagementDto> getTasksByAssigneeId(Long assigneeId);
	   List<TaskManagementDto> getTasksByReferenceIdAndType(Long referenceId, ReferenceType referenceType);
	   List<TaskManagementDto> fetchSmartTasksByDate(TaskFetchByDataRequest request);
	   String updateTaskPriority(TaskPriorityUpdateRequest request);
	   List<TaskManagementDto> getTasksByPriority(Priority priority);
	   String addComment(Long taskId, Long AssigneeId, String commentText);
}
