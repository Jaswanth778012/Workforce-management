package com.railse.hiring.workforcemgmt.dto;

import lombok.Data;

@Data
public class TaskCommentRequedt {
	private long taskId;
	private long assigneeId;
	private String comment;
}
