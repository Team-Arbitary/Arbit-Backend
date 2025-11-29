package com.uom.Software_design_competition.application.transport.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThermalInspectionReportRequest {
    private Long id;
    private Long inspectionId;
    private String transformerNo;
    private String reportData; // JSON string containing full form data
    private String createdBy;
}
