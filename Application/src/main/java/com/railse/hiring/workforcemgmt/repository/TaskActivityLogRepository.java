package com.railse.hiring.workforcemgmt.repository;

import java.util.List;

import com.railse.hiring.workforcemgmt.model.TaskActivityLog;

public interface TaskActivityLogRepository {
	TaskActivityLog save(TaskActivityLog log);
    List<TaskActivityLog> findByTaskId(Long taskId);
}
