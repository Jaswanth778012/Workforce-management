package com.railse.hiring.workforcemgmt.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.railse.hiring.worforcemgmt.common.model.enums.Priority;
import com.railse.hiring.worforcemgmt.common.model.enums.ReferenceType;
import com.railse.hiring.worforcemgmt.common.model.enums.Task;
import com.railse.hiring.worforcemgmt.common.model.enums.TaskStatus;
import com.railse.hiring.workforcemgmt.common.exception.ResourceNotFoundException;
import com.railse.hiring.workforcemgmt.dto.AssignByRefferenceRequest;
import com.railse.hiring.workforcemgmt.dto.TaskCreateRequest;
import com.railse.hiring.workforcemgmt.dto.TaskFetchByDataRequest;
import com.railse.hiring.workforcemgmt.dto.TaskManagementDto;
import com.railse.hiring.workforcemgmt.dto.TaskPriorityUpdateRequest;
import com.railse.hiring.workforcemgmt.dto.UpdateTaskRequest;
import com.railse.hiring.workforcemgmt.mapper.ITaskManagementMapper;
import com.railse.hiring.workforcemgmt.model.TaskActivityLog;
import com.railse.hiring.workforcemgmt.model.TaskComment;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.repository.TaskActivityLogRepository;
import com.railse.hiring.workforcemgmt.repository.TaskCommentRepository;
import com.railse.hiring.workforcemgmt.repository.TaskRepository;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;

@Service
public class TaskManagementServiceImpl implements TaskManagementService {
	 private final TaskRepository taskRepository;
	   private final ITaskManagementMapper taskMapper;
	    private final TaskCommentRepository commentRepository;
	    private final TaskActivityLogRepository activityLogRepository;


	   public TaskManagementServiceImpl(TaskRepository taskRepository, ITaskManagementMapper taskMapper,
	                                    TaskCommentRepository commentRepository,
	                                    TaskActivityLogRepository activityLogRepository) {
		   	       this.commentRepository = commentRepository;
		   	       	       this.activityLogRepository = activityLogRepository;
	       this.taskRepository = taskRepository;
	       this.taskMapper = taskMapper;
	   }


	   @Override
	   public TaskManagementDto findTaskById(Long id) {
	       TaskManagement task = taskRepository.findById(id)
	               .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
	       TaskManagementDto dto = taskMapper.modelToDto(task);
	       dto.setComments(commentRepository.findByTaskId(id));
	       dto.setActivityLogs(activityLogRepository.findByTaskId(id)
	           .stream()
	           .sorted(Comparator.comparing(TaskActivityLog::getTimestamp))
	           .collect(Collectors.toList())
	       );
	       return dto;
	   }


	   @Override
	   public List<TaskManagementDto> createTasks(TaskCreateRequest createRequest) {
	       List<TaskManagement> createdTasks = new ArrayList<>();
	       for (TaskCreateRequest.RequestItem item : createRequest.getRequests()) {
	           TaskManagement newTask = new TaskManagement();
	           newTask.setReferenceId(item.getReferenceId());
	           newTask.setReferenceType(item.getReferenceType());
	           newTask.setTask(item.getTask());
	           newTask.setAssigneeId(item.getAssigneeId());
	           newTask.setPriority(item.getPriority());
	           newTask.setTaskDeadlineTime(item.getTaskDeadlineTime());
	           newTask.setStatus(TaskStatus.ASSIGNED);
	           newTask.setDescription("New task created.");
	           TaskManagement savedTask = taskRepository.save(newTask);
	           createdTasks.add(savedTask);
		       logActivity(savedTask.getId(), "Task created and assigned to user " + savedTask.getAssigneeId());
	       }

	       return taskMapper.modelListToDtoList(createdTasks);
	   }


	   @Override
	   public List<TaskManagementDto> updateTasks(UpdateTaskRequest updateRequest) {
	       List<TaskManagement> updatedTasks = new ArrayList<>();
	       for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
	    	    TaskManagement task = taskRepository.findById(item.getTaskId())
	    	        .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));

	    	    boolean updated = false;
	    	    StringBuilder changes = new StringBuilder("Updated task: ");

	    	    if (item.getTaskStatus() != null && task.getStatus() != item.getTaskStatus()) {
	    	        changes.append("Status changed from ").append(task.getStatus())
	    	               .append(" to ").append(item.getTaskStatus()).append(". ");
	    	        task.setStatus(item.getTaskStatus());
	    	        updated = true;
	    	    }
	    	    if (item.getDescription() != null && !item.getDescription().equals(task.getDescription())) {
	    	        changes.append("Description updated. ");
	    	        task.setDescription(item.getDescription());
	    	        updated = true;
	    	    }
	    	    TaskManagement savedTask = taskRepository.save(task);
	    	    updatedTasks.add(savedTask);
	    	    
	    	    if (updated) {
	    	        logActivity(savedTask.getId(), changes.toString());
	    	    }
	    	}

	       return taskMapper.modelListToDtoList(updatedTasks);
	   }


//	   @Override
//	   public String assignByReference(AssignByRefferenceRequest request) {
//	       List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
//	       List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(request.getReferenceId(), request.getReferenceType());
//
//
//	       for (Task taskType : applicableTasks) {
//	           List<TaskManagement> tasksOfType = existingTasks.stream()
//	                   .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
//	                   .collect(Collectors.toList());
//
//
//	           // BUG #1 is here. It should assign one and cancel the rest.
//	           // Instead, it reassigns ALL of them.
//	           if (!tasksOfType.isEmpty()) {
//	               for (TaskManagement taskToUpdate : tasksOfType) {
//	                   taskToUpdate.setAssigneeId(request.getAssigneeId());
//	                   taskRepository.save(taskToUpdate);
//	               }
//	           } else {
//	               // Create a new task if none exist
//	               TaskManagement newTask = new TaskManagement();
//	               newTask.setReferenceId(request.getReferenceId());
//	               newTask.setReferenceType(request.getReferenceType());
//	               newTask.setTask(taskType);
//	               newTask.setAssigneeId(request.getAssigneeId());
//	               newTask.setStatus(TaskStatus.ASSIGNED);
//	               taskRepository.save(newTask);
//	           }
//	       }
//	       return "Tasks assigned successfully for reference " + request.getReferenceId();
//	   }
	   
	   @Override
	   public String assignByReference(AssignByRefferenceRequest request) {

	       List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
	       List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(
	               request.getReferenceId(), request.getReferenceType());


	       for (Task taskType : applicableTasks) {
	           List<TaskManagement> tasksOfType = existingTasks.stream()
	                   .filter(t -> t.getTask().equals(taskType) && t.getStatus() != TaskStatus.COMPLETED)
	                   .collect(Collectors.toList());


	           // Find if task already assigned to the new assignee
	           Optional<TaskManagement> assignedTaskOpt = tasksOfType.stream()
	                   .filter(t -> t.getAssigneeId().equals(request.getAssigneeId()) && t.getStatus() == TaskStatus.ASSIGNED)
	                   .findFirst();

	           if (assignedTaskOpt.isPresent()) {
	               System.out.println("Task already assigned to target assignee for taskType: " + taskType);
	               // Cancel all other assigned tasks of this type assigned to others
	               for (TaskManagement task : tasksOfType) {
	                   if (!task.getAssigneeId().equals(request.getAssigneeId()) && task.getStatus() == TaskStatus.ASSIGNED) {
	                       System.out.println("Cancelling task id: " + task.getId());
	                       task.setStatus(TaskStatus.CANCELLED);
	                       task.setDescription("Cancelled due to reassignment.");
	                       taskRepository.save(task);
	                       logActivity(task.getId(), "Task reassigned. Cancelled assignment for user " + task.getAssigneeId());
	                   }
	               }
	           } else {
	               for (TaskManagement task : tasksOfType) {
	                   if (task.getStatus() == TaskStatus.ASSIGNED) {
	                       System.out.println("Cancelling task id: " + task.getId());
	                       task.setStatus(TaskStatus.CANCELLED);
	                       task.setDescription("Cancelled due to reassignment.");
	                       taskRepository.save(task);
	                       logActivity(task.getId(), "Task reassigned. Cancelled assignment for user " + task.getAssigneeId());
	                   }
	               }
	               TaskManagement newTask = new TaskManagement();
	               newTask.setReferenceId(request.getReferenceId());
	               newTask.setReferenceType(request.getReferenceType());
	               newTask.setTask(taskType);
	               newTask.setAssigneeId(request.getAssigneeId());
	               newTask.setStatus(TaskStatus.ASSIGNED);
	               newTask.setDescription("Task assigned to new assignee.");
	               taskRepository.save(newTask);
	               logActivity(newTask.getId(), "Task reassigned to user " + request.getAssigneeId());
	           }
	       }

	       return "Tasks reassigned successfully for reference " + request.getReferenceId();
	   }



//	   @Override
//	   public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDataRequest request) {
//	       List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());
//
//
//	       // BUG #2 is here. It should filter out CANCELLED tasks but doesn't.
//	       List<TaskManagement> filteredTasks = tasks.stream()
//	               .filter(task -> {
//	                   // This logic is incomplete for the assignment.
//	                   // It should check against startDate and endDate.
//	                   // For now, it just returns all tasks for the assignees.
//	                   return true;
//	               })
//	               .collect(Collectors.toList());
//
//
//	       return taskMapper.modelListToDtoList(filteredTasks);
//	   }
	   @Override
	   public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDataRequest request) {
	       List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());

	       List<TaskManagement> filteredTasks = tasks.stream()
	           .filter(task -> 
	               task.getTaskDeadlineTime() != null &&
	               task.getTaskDeadlineTime() >= request.getStartDate() &&
	               task.getTaskDeadlineTime() <= request.getEndDate() &&
	               !TaskStatus.CANCELLED.equals(task.getStatus()) // compare enums directly
	           )
	           .collect(Collectors.toList());

	       return taskMapper.modelListToDtoList(filteredTasks);
	   }


	   //Feature 1
	   @Override
	   public List<TaskManagementDto> fetchSmartTasksByDate(TaskFetchByDataRequest request) {
	       List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());

	       List<TaskManagement> filteredTasks = tasks.stream()
	           .filter(task -> {
	               if (task.getTaskDeadlineTime() == null) return false;

	               boolean isActiveStatus = !(task.getStatus() == TaskStatus.CANCELLED || task.getStatus() == TaskStatus.COMPLETED);
	               boolean deadlineInRange = task.getTaskDeadlineTime() >= request.getStartDate() &&
	                                         task.getTaskDeadlineTime() <= request.getEndDate();
	               boolean deadlineAfterRange = task.getTaskDeadlineTime() > request.getEndDate();

	               return isActiveStatus && (deadlineInRange || deadlineAfterRange);
	           })
	           .collect(Collectors.toList());

	       return taskMapper.modelListToDtoList(filteredTasks);
	   }



	   
	   @Override
	   public List<TaskManagementDto> findAllTasks() {
	       List<TaskManagement> allTasks = taskRepository.findAll();
	       List<TaskManagementDto> dtos = taskMapper.modelListToDtoList(allTasks);

	       for (TaskManagementDto dto : dtos) {
	           dto.setComments(commentRepository.findByTaskId(dto.getId()));
	           dto.setActivityLogs(activityLogRepository.findByTaskId(dto.getId()));
	       }

	       return dtos;
	   }
	   
	   @Override
	   public List<TaskManagementDto> getTasksByAssigneeId(Long assigneeId) {
	       List<TaskManagement> tasks = taskRepository.findByAssigneeId(assigneeId);
	       return taskMapper.modelListToDtoList(tasks);
	   }
	   
	   
	   @Override
	   public List<TaskManagementDto> getTasksByReferenceIdAndType(Long referenceId, ReferenceType referenceType) {
	       List<TaskManagement> tasks = taskRepository.findByReferenceIdAndReferenceType(referenceId, referenceType);
	       System.out.println("Tasks found: " + tasks.size());
	       List<TaskManagementDto> dtos = taskMapper.modelListToDtoList(tasks);
	       System.out.println("Mapped DTOs size: " + dtos.size());

	       for (TaskManagementDto dto : dtos) {
	    	    List<TaskComment> comments = commentRepository.findByTaskId(dto.getId());
	    	    System.out.println("Comments for task " + dto.getId() + ": " + comments.size());
	    	    dto.setComments(comments);

	    	    List<TaskActivityLog> logs = activityLogRepository.findByTaskId(dto.getId());
	    	    System.out.println("Activity logs for task " + dto.getId() + ": " + logs.size());
	    	    dto.setActivityLogs(logs);
	    	}

	       return dtos;
	   }

	   
	   //Feature 2
	   @Override
	   public String updateTaskPriority(TaskPriorityUpdateRequest request) {
	       TaskManagement task = taskRepository.findById(request.getTaskId())
	           .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + request.getTaskId()));

	       task.setPriority(request.getPriority());
	       taskRepository.save(task);
	       logActivity(task.getId(), "Priority changed to " + request.getPriority());

	       return "Task priority updated successfully.";
	       
	   }

	   @Override
	   public List<TaskManagementDto> getTasksByPriority(Priority priority) {
	       List<TaskManagement> tasks = taskRepository.findByPriority(priority);
	       return taskMapper.modelListToDtoList(tasks);
	   }
	   
	   //Feature 3
	   private void logActivity(Long taskId, String message) {
	        TaskActivityLog log = new TaskActivityLog();
	        log.setTaskId(taskId);
	        log.setMessage(message);
	        log.setTimestamp(System.currentTimeMillis());
	        activityLogRepository.save(log);
	    }

	   @Override
	   public String addComment(Long taskId, Long AssigneeId, String commentText) {
	       TaskManagement task = taskRepository.findById(taskId)
	           .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

	       TaskComment comment = new TaskComment();
	       comment.setTaskId(taskId);
	       comment.setAssigneeId(AssigneeId);
	       comment.setComment(commentText);
	       comment.setTimestamp(System.currentTimeMillis());
	       commentRepository.save(comment);

	       logActivity(taskId, "User " +AssigneeId + " added a comment.");
	       
	       return "Comment added successfully.";
	   }
	   
	   

}
