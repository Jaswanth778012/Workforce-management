package com.railse.hiring.workforcemgmt.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.railse.hiring.worforcemgmt.common.model.enums.Task;
import com.railse.hiring.worforcemgmt.common.model.enums.TaskStatus;
import com.railse.hiring.workforcemgmt.common.exception.ResourceNotFoundException;
import com.railse.hiring.workforcemgmt.dto.AssignByRefferenceRequest;
import com.railse.hiring.workforcemgmt.dto.TaskCreateRequest;
import com.railse.hiring.workforcemgmt.dto.TaskFetchByDataRequest;
import com.railse.hiring.workforcemgmt.dto.TaskManagementDto;
import com.railse.hiring.workforcemgmt.dto.UpdateTaskRequest;
import com.railse.hiring.workforcemgmt.mapper.ITaskManagementMapper;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.repository.TaskRepository;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;

@Service
public class TaskManagementServiceImpl implements TaskManagementService {
	 private final TaskRepository taskRepository;
	   private final ITaskManagementMapper taskMapper;


	   public TaskManagementServiceImpl(TaskRepository taskRepository, ITaskManagementMapper taskMapper) {
	       this.taskRepository = taskRepository;
	       this.taskMapper = taskMapper;
	   }


	   @Override
	   public TaskManagementDto findTaskById(Long id) {
	       TaskManagement task = taskRepository.findById(id)
	               .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
	       return taskMapper.modelToDto(task);
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
	           createdTasks.add(taskRepository.save(newTask));
	       }
	       return taskMapper.modelListToDtoList(createdTasks);
	   }


	   @Override
	   public List<TaskManagementDto> updateTasks(UpdateTaskRequest updateRequest) {
	       List<TaskManagement> updatedTasks = new ArrayList<>();
	       for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
	           TaskManagement task = taskRepository.findById(item.getTaskId())
	                   .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));


	           if (item.getTaskStatus() != null) {
	               task.setStatus(item.getTaskStatus());
	           }
	           if (item.getDescription() != null) {
	               task.setDescription(item.getDescription());
	           }
	           updatedTasks.add(taskRepository.save(task));
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
	                   }
	               }
	           } else {
	               for (TaskManagement task : tasksOfType) {
	                   if (task.getStatus() == TaskStatus.ASSIGNED) {
	                       System.out.println("Cancelling task id: " + task.getId());
	                       task.setStatus(TaskStatus.CANCELLED);
	                       task.setDescription("Cancelled due to reassignment.");
	                       taskRepository.save(task);
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
	       return taskMapper.modelListToDtoList(allTasks);
	   }
	   
	   @Override
	   public List<TaskManagementDto> getTasksByAssigneeId(Long assigneeId) {
	       List<TaskManagement> tasks = taskRepository.findByAssigneeId(assigneeId);
	       return taskMapper.modelListToDtoList(tasks);
	   }


}
