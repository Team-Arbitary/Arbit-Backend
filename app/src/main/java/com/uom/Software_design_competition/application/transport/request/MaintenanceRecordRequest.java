package com.uom.Software_design_competition.application.transport.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRecordRequest {
    private Long id; // optional if updating by ID
    private Long inspectionId;

    // New fields from frontend
    private String inspectorName;
    private String status;
    private String voltageReading;
    private String currentReading;
    private String recommendedAction;
    private String remarks;
    private String reportData; // JSON string containing full form data

    // Maintenance Personnel & Timings (legacy)
    private String startTime;
    private String completionTime;
    private String supervisedBy;

    // Technicians & Helpers
    private String techI;
    private String techII;
    private String techIII;
    private String helpers;

    // Inspection Sign-offs
    private String inspectedBy;
    private String inspectedByDate;
    private String rectifiedBy;
    private String rectifiedByDate;
    private String reInspectedBy;
    private String reInspectedByDate;

    // CSS
    private String css;
    private String cssDate;
}