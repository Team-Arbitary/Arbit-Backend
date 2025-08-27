package com.uom.Software_design_competition.application.transport.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspectionRecordsRequest {
    private String inspectionNo;
    private String branch;
    private String transformerNo;
    private String dateOfInspection;
    private String time;
    private String maintenanceDate;
    private String status;

}