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
    private String id;
    private String batch;
    private String transformerNo;
    private LocalDate dateOfInspection;
    private LocalTime time;
}