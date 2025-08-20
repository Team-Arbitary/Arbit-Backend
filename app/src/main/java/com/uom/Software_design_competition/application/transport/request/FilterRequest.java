package com.uom.Software_design_competition.application.transport.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterRequest {
    private List<FilterValue> filterValues;
    private String tableTemplateId;
    private Boolean defaultTableTemplate;
    @NotNull(message = "Offset is mandatory")
    @Min(value = 0, message = "Offset must be 0 or greater")
    private Integer offset;
    @NotNull(message = "Limit is mandatory")
    @Min(value = 1, message = "Limit must be 1 or greater")
    private Integer limit;
}
