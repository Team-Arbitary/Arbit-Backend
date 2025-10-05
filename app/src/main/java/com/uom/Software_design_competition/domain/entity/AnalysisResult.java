package com.uom.Software_design_competition.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inspection_no", nullable = false, unique = true)
    private String inspectionNo;

    @Column(name = "transformer_no", nullable = false)
    private String transformerNo;

    @Column(name = "annotated_image_data", columnDefinition = "BYTEA")
    private byte[] annotatedImageData;

    @Column(name = "analysis_date", nullable = false)
    private LocalDateTime analysisDate;

    @Column(name = "analysis_status", nullable = false)
    private String analysisStatus; // SUCCESS, FAILED, IN_PROGRESS

    @Column(name = "analysis_result_json", columnDefinition = "TEXT")
    private String analysisResultJson; // JSON response from analysis API

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    // Constructor for creating new analysis result
    public AnalysisResult(String inspectionNo, String transformerNo, byte[] annotatedImageData, 
                         String analysisResultJson, String analysisStatus) {
        this.inspectionNo = inspectionNo;
        this.transformerNo = transformerNo;
        this.annotatedImageData = annotatedImageData;
        this.analysisResultJson = analysisResultJson;
        this.analysisStatus = analysisStatus;
        this.analysisDate = LocalDateTime.now();
    }
}