package com.uom.Software_design_competition.application.transport.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThermalInspectionReportResponse {
    private Long id;
    private Long inspectionId;
    private String transformerNo;
    private String reportData; // JSON string containing full form data
    
    // Version/History info
    private Integer version;
    private Boolean isCurrent;
    private String createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
