package com.uom.Software_design_competition.application.transport.request;

import lombok.Data;

@Data
public class InspectionImageRequest {
    private String inspectionId;
    private String name;
    private String type;
    private byte[] data;
    private String uploader;
    private String environmentCondition; // Sunny, Cloudy, Rainy
}