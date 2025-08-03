package com.railse.hiring.workforcemgmt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.railse.hiring.worforcemgmt.common.model.enums.Priority;
import com.railse.hiring.worforcemgmt.common.model.enums.ReferenceType;
import com.railse.hiring.workforcemgmt.common.model.response.Response;
import com.railse.hiring.workforcemgmt.dto.AssignByRefferenceRequest;
import com.railse.hiring.workforcemgmt.dto.TaskCommentRequedt;
import com.railse.hiring.workforcemgmt.dto.TaskCreateRequest;
import com.railse.hiring.workforcemgmt.dto.TaskFetchByDataRequest;
import com.railse.hiring.workforcemgmt.dto.TaskManagementDto;
import com.railse.hiring.workforcemgmt.dto.TaskPriorityUpdateRequest;
import com.railse.hiring.workforcemgmt.dto.UpdateTaskRequest;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;

@RestController
@RequestMapping("/task-mgmt")
public class TaskManagementController {


	   private final TaskManagementService taskManagementService;


	   public TaskManagementController(TaskManagementService taskManagementService) {
	       this.taskManagementService = taskManagementService;
	   }


	   @GetMapping("/{id}")
	   public Response<TaskManagementDto> getTaskById(@PathVariable Long id) {
	       return new Response<>(taskManagementService.findTaskById(id));
	   }


	   @PostMapping("/create")
	   public Response<List<TaskManagementDto>> createTasks(@RequestBody TaskCreateRequest request) {
	       return new Response<>(taskManagementService.createTasks(request));
	   }


	   @PostMapping("/update")
	   public Response<List<TaskManagementDto>> updateTasks(@RequestBody UpdateTaskRequest request) {
	       return new Response<>(taskManagementService.updateTasks(request));
	   }


	   @PostMapping("/assign-by-ref")
	   public Response<String> assignByReference(@RequestBody AssignByRefferenceRequest request) {
	       return new Response<>(taskManagementService.assignByReference(request));
	   }



	   @PostMapping("/fetch-by-date/v2")
	   public Response<List<TaskManagementDto>> fetchByDate(@RequestBody TaskFetchByDataRequest request) {
	       return new Response<>(taskManagementService.fetchTasksByDate(request));
	   }
	   
	   //Feature 1
	   @PostMapping("/fetch-smart")
	    public ResponseEntity<List<TaskManagementDto>> fetchSmartTasksByDate(@RequestBody TaskFetchByDataRequest request) {
	        List<TaskManagementDto> tasks = taskManagementService.fetchSmartTasksByDate(request);
	        return ResponseEntity.ok(tasks);
	    }
	   
	   @GetMapping("/all")
	   public Response<List<TaskManagementDto>> getAllTasks() {
	       return new Response<>(taskManagementService.findAllTasks());
	   }
	   
	   @GetMapping("/assignee/{assigneeId}")
	   public Response<List<TaskManagementDto>> getTasksByAssignee(@PathVariable Long assigneeId) {
	       return new Response<>(taskManagementService.getTasksByAssigneeId(assigneeId));
	   }
	   
	   @GetMapping("/tasks/by-reference")
	   public ResponseEntity<List<TaskManagementDto>> getTasksByReference(
	           @RequestParam Long referenceId,
	           @RequestParam ReferenceType referenceType) {

	       List<TaskManagementDto> tasks = taskManagementService.getTasksByReferenceIdAndType(referenceId, referenceType);
	       return ResponseEntity.ok(tasks);
	   }

	   
	   //Feature 2
	   @PutMapping("/update-priority")
	   public ResponseEntity<String> updateTaskPriority(@RequestBody TaskPriorityUpdateRequest request) {
	       String response = taskManagementService.updateTaskPriority(request);
	       return ResponseEntity.ok(response);
	   }

	   @GetMapping("/priority/{priority}")
	   public ResponseEntity<List<TaskManagementDto>> getTasksByPriority(@PathVariable Priority priority) {
	       List<TaskManagementDto> tasks = taskManagementService.getTasksByPriority(priority);
	       return ResponseEntity.ok(tasks);
	   }
	   
	   //feature 3
	   @PostMapping("/task/comment")
	   public ResponseEntity<String> addComment(@RequestBody TaskCommentRequedt request) {
	       String result = taskManagementService.addComment(
	           request.getTaskId(), request.getAssigneeId(), request.getComment());
	       return ResponseEntity.ok(result);
	   }


}
