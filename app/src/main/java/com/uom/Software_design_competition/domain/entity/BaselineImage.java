package com.uom.Software_design_competition.domain.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "baseline_images")
public class BaselineImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transformerId; // Associated Transformer ID
    private String name;
    private String type;
    private byte[] data;

    // Metadata fields
    private LocalDateTime uploadDateTime;
    private String uploader;
    private String environmentCondition; // Sunny, Cloudy, Rainy
}