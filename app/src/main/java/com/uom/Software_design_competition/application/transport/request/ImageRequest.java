package com.uom.Software_design_competition.application.transport.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {
    private String transformerNo;
    private String inspectionNo;
    private String imageType; // "Baseline" or "Thermal"
    private String weatherCondition; // "Sunny", "Cloudy", "Rainy"
    private String status;
    private MultipartFile imageFile;
}