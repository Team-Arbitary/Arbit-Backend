package com.uom.Software_design_competition.domain.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "thermal_inspection_reports")
public class ThermalInspectionReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inspection_id", referencedColumnName = "id")
    private InspectionRecords inspection;

    @Column(name = "transformer_no")
    private String transformerNo;

    // Store full report data as JSON
    @Column(name = "report_data", columnDefinition = "TEXT")
    private String reportData;

    // Version for history management
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Integer version = 1;

    // Flag to indicate current/latest version
    @Column(name = "is_current", nullable = false)
    @Builder.Default
    private Boolean isCurrent = true;

    // Created by user reference
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (version == null) version = 1;
        if (isCurrent == null) isCurrent = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
