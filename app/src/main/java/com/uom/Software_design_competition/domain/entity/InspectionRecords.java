package com.uom.Software_design_competition.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "inspection_records")
@SequenceGenerator(name = "inspection_seq_gen", sequenceName = "inspection_sequence", allocationSize = 1)
public class InspectionRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inspection_seq")
    @SequenceGenerator(name = "inspection_seq", sequenceName = "inspection_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "inspection_no")
    private String inspectionNo;

    private String branch;

    @Column(name = "transformer_no")
    private String transformerNo;

    @Column(name = "date_of_inspection")
    private String dateOfInspection;

    private String time;

    @Column(name = "maintenance_date")
    private String maintenanceDate;

    @Builder.Default
    private String status = "Not started";

    // Method to generate inspection number
    @PrePersist
    public void generateInspectionNo() {
        if (this.inspectionNo == null) {
            // This will be handled by the service layer
        }
    }
}