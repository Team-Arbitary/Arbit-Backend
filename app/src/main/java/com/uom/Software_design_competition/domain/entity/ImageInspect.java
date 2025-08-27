package com.uom.Software_design_competition.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "image_inspect")
public class ImageInspect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transformer_no")
    private String transformerNo;

    @Column(name = "inspection_no")
    private String inspectionNo;

    @Column(name = "image_type")
    private String imageType; // "Baseline" or "Thermal"

    @Column(name = "weather_condition")
    private String weatherCondition; // "Sunny", "Cloudy", "Rainy"

    @Column(name = "upload_date")
    private String uploadDate;

    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    private String status; // "Not started", "Pending", "In progress", "Completed"
}