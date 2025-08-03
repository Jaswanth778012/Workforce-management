package com.railse.hiring.workforcemgmt.repository;

import java.util.List;

import com.railse.hiring.workforcemgmt.model.TaskComment;

public interface TaskCommentRepository {

	TaskComment save(TaskComment comment);
    List<TaskComment> findByTaskId(Long taskId);
}
