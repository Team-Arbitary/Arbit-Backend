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
public class MaintenanceRecordResponse {
    private Long id;
    private Long inspectionId;
    private String transformerNo;

    // Maintenance Personnel & Timings
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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}