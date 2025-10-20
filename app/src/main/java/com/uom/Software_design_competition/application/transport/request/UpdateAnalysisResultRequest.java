package com.uom.Software_design_competition.application.transport.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAnalysisResultRequest {
    
    private String inspectionNo;
    private String transformerNo;
    private String analysisResultJson; // The updated JSON from frontend
}
