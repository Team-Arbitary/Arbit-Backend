package com.uom.Software_design_competition.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transformer_records")
public class TransformerRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "transformer_no")
    private String transformerNo;
    @Column(name = "pole_no")
    private String poleNo;
    private String regions;     
    private String type;
    private String location;

}