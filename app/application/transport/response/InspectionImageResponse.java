package com.uom.Software_design_competition.application.transport.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InspectionImageResponse {
    private String id;
    private String inspectionId;
    private String name;
    private String type;
    private String uploader;
    private String environmentCondition;
}