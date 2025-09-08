package com.uom.Software_design_competition.application.transport.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransformerRecordsRequest {
    
    @NotBlank(message = "Regions cannot be blank")
    @Size(max = 100, message = "Regions must not exceed 100 characters")
    private String regions;
    
    @NotBlank(message = "Pole number cannot be blank")
    @Size(max = 50, message = "Pole number must not exceed 50 characters")
    private String poleNo;
    
    @NotBlank(message = "Transformer number cannot be blank")
    @Size(max = 50, message = "Transformer number must not exceed 50 characters")
    private String transformerNo;
    
    @NotBlank(message = "Type cannot be blank")
    @Size(max = 50, message = "Type must not exceed 50 characters")
    private String type;
    
    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;
}