package com.railse.hiring.workforcemgmt.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.railse.hiring.worforcemgmt.common.model.enums.Priority;
import com.railse.hiring.worforcemgmt.common.model.enums.ReferenceType;
import com.railse.hiring.worforcemgmt.common.model.enums.Task;
import com.railse.hiring.worforcemgmt.common.model.enums.TaskStatus;
import com.railse.hiring.workforcemgmt.model.TaskActivityLog;
import com.railse.hiring.workforcemgmt.model.TaskComment;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskManagementDto {
	   private Long id;
	   private Long referenceId;
	   private ReferenceType referenceType;
	   private Task task;
	   private String description;
	   private TaskStatus status;
	   private Long assigneeId;
	   private Long taskDeadlineTime;
	   private Priority priority;
	   private List<TaskComment> comments;
	    private List<TaskActivityLog> activityLogs;

	   
}
