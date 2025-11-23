package com.uom.Software_design_competition.application.transport.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MaintenanceRecordResponse {
    private Long id;
    private Long inspectionId;
    private String transformerNo;
    private String inspectorName;
    private String status;
    private String voltageReading;
    private String currentReading;
    private String recommendedAction;
    private String remarks;
    private String reportData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
