package com.railse.hiring.workforcemgmt.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.railse.hiring.workforcemgmt.model.TaskActivityLog;

@Repository
public class InMemoryTaskActivityRepository implements TaskActivityLogRepository{
	
	 private final Map<Long, List<TaskActivityLog>> store = new ConcurrentHashMap<>();
	    private final AtomicLong idGenerator = new AtomicLong(1);

	    @Override
	    public TaskActivityLog save(TaskActivityLog log) {
	        log.setId(idGenerator.getAndIncrement());
	        store.computeIfAbsent(log.getTaskId(), k -> new ArrayList<>()).add(log);
	        return log;
	    }

	    @Override
	    public List<TaskActivityLog> findByTaskId(Long taskId) {
	        return store.getOrDefault(taskId, Collections.emptyList());
	    }
	
}
