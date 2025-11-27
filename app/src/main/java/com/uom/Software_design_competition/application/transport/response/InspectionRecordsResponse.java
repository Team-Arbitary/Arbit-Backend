package com.uom.Software_design_competition.application.transport.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspectionRecordsResponse {
    private Long id;
    private String inspectionNo;
    private String branch;
    private String transformerNo;
    private String dateOfInspection;
    private String time;
    private String maintenanceDate;
    private String status;

    // Engineer fields
    private String inspectorName;
    private String engineerStatus;
    private String voltage;
    private String current;
    private String recommendedAction;
    private String additionalRemarks;

    // Thermal image paths or URLs
    private String irLeft;
    private String irRight;
    private String irFront;

}