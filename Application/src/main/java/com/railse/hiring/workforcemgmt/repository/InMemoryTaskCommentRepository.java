package com.railse.hiring.workforcemgmt.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.railse.hiring.workforcemgmt.model.TaskComment;

@Repository
public class InMemoryTaskCommentRepository implements TaskCommentRepository {

    private final Map<Long, List<TaskComment>> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public TaskComment save(TaskComment comment) {
        comment.setId(idGenerator.getAndIncrement());
        store.computeIfAbsent(comment.getTaskId(), k -> new ArrayList<>()).add(comment);
        return comment;
    }
    
    @Override
    public List<TaskComment> findByTaskId(Long taskId) {
        return store.getOrDefault(taskId, Collections.emptyList());
    }

//	@Override
//	public List<String> getComments(String taskId) {
//		// Implementation for retrieving comments for a task
//		return new ArrayList<>();
//	}
//
//	@Override
//	public void deleteComment(String taskId, String commentId) {
//		// Implementation for deleting a specific comment from a task
//	}

}
