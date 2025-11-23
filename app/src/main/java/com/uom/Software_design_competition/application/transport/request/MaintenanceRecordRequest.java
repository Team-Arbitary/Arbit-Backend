package com.uom.Software_design_competition.application.transport.request;

import lombok.Data;

@Data
public class MaintenanceRecordRequest {
    private Long inspectionId;
    private String inspectorName;
    private String status;
    private String voltageReading;
    private String currentReading;
    private String recommendedAction;
    private String remarks;
    private String reportData;
}
