package com.uom.Software_design_competition.application.transport.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransformerRecordsResponse {
    private Long id;
    private String regions;
    private String poleNo;
    private String transformerNo;
    private String type;
    private String location;

}