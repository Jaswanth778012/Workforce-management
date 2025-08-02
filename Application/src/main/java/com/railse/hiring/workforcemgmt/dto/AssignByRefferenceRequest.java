package com.railse.hiring.workforcemgmt.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.railse.hiring.worforcemgmt.common.model.enums.ReferenceType;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AssignByRefferenceRequest {
	private Long referenceId;
	   private ReferenceType referenceType;
	   private Long assigneeId;
}
