
package com.uom.Software_design_competition.application.controller;

import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class BaseController {

    public <T> ResponseEntity<ApiResponse<T>> setResponseEntity(ApiResponse<T> ApiResponse){
        String responseCode = ApiResponse.getResponseCode();
        switch (responseCode) {
            case "2000":
            case "2001":
                return ResponseEntity.status(HttpStatus.OK).body(ApiResponse);
            case "2007":
                return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(ApiResponse);
            case "4000":
            case "4001":
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse);
            case "4003":
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ApiResponse);
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse);
        }
    }
}
