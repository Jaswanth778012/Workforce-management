package com.railse.hiring.workforcemgmt.repository;

import java.util.List;
import java.util.Optional;

import com.railse.hiring.worforcemgmt.common.model.enums.ReferenceType;
import com.railse.hiring.workforcemgmt.model.TaskManagement;

public interface TaskRepository {
	   Optional<TaskManagement> findById(Long id);
	   TaskManagement save(TaskManagement task);
	   List<TaskManagement> findAll();
	   List<TaskManagement> findByReferenceIdAndReferenceType(Long referenceId, ReferenceType referenceType);
	   List<TaskManagement> findByAssigneeIdIn(List<Long> assigneeIds);
	   List<TaskManagement> findByAssigneeId(Long assigneeId);

	}

