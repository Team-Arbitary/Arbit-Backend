package com.uom.Software_design_competition.application.transport.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaselineImageResponse {
    private Long id;
    private String transformerId;
    private String name;
    private String type;
    private String uploader;
    private String environmentCondition;
}