package com.uom.Software_design_competition.application.transport.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageInspectResponse {
    private Long id;
    private String transformerNo;
    private String inspectionNo;
    private String imageType;
    private String weatherCondition;
    private String uploadDate;
    private String status;
  //  private String createdAt;
    private String imageBase64; // For displaying image
}