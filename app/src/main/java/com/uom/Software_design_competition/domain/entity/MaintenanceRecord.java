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
@Table(name = "maintenance_records")
public class MaintenanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inspection_id", referencedColumnName = "id")
    private InspectionRecords inspection;

    // Inspector info
    @Column(name = "inspector_name")
    private String inspectorName;

    @Column(name = "status")
    private String status;

    @Column(name = "voltage_reading")
    private String voltageReading;

    @Column(name = "current_reading")
    private String currentReading;

    @Column(name = "recommended_action", columnDefinition = "TEXT")
    private String recommendedAction;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

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

    // Maintenance Personnel & Timings (legacy fields, kept for backward compatibility)
    @Column(name = "start_time")
    private String startTime;

    @Column(name = "completion_time")
    private String completionTime;

    @Column(name = "supervised_by")
    private String supervisedBy;

    // Technicians & Helpers
    @Column(name = "tech_i")
    private String techI;

    @Column(name = "tech_ii")
    private String techII;

    @Column(name = "tech_iii")
    private String techIII;

    private String helpers;

    // Inspection Sign-offs
    @Column(name = "inspected_by")
    private String inspectedBy;

    @Column(name = "inspected_by_date")
    private String inspectedByDate;

    @Column(name = "rectified_by")
    private String rectifiedBy;

    @Column(name = "rectified_by_date")
    private String rectifiedByDate;

    @Column(name = "re_inspected_by")
    private String reInspectedBy;

    @Column(name = "re_inspected_by_date")
    private String reInspectedByDate;

    // CSS
    private String css;

    @Column(name = "css_date")
    private String cssDate;

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