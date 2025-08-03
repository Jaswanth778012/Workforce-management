package com.railse.hiring.workforcemgmt.model;

import lombok.Data;

@Data
public class TaskActivityLog {
    private Long id;
    private Long taskId;
    private String message;
    private Long timestamp;

    // Getters & setters
}

