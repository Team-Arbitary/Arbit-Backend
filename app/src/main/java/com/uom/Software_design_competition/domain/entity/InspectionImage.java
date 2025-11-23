package com.uom.Software_design_competition.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inspection_images")
public class InspectionImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String inspectionId; // Associated Inspection Record ID
    private String name;
    private String type;
    private byte[] data;

    // Metadata fields
    private LocalDateTime uploadDateTime;
    private String uploader;
    private String environmentCondition; // Sunny, Cloudy, Rainy
}