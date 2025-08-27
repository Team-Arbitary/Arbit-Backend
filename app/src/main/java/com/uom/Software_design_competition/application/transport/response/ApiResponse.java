package com.uom.Software_design_competition.application.transport.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String responseCode;
    private String responseDescription;
    private T responseData;
    private PageDetail pageDetail;

    public ApiResponse(String responseCode, String responseDescription, T responseData) {
        this.responseCode = responseCode;
        this.responseDescription = responseDescription;
        this.responseData = responseData;
    }

    public ApiResponse(String responseCode, String responseDescription) {
        this.responseCode = responseCode;
        this.responseDescription = responseDescription;
    }
}