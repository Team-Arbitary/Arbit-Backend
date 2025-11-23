package com.uom.Software_design_competition.application.controller;

import com.uom.Software_design_competition.application.transport.request.MaintenanceRecordRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.MaintenanceRecordResponse;
import com.uom.Software_design_competition.domain.service.MaintenanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${base-url.context}/api/maintenance-records")
public class MaintenanceRecordController {

    @Autowired
    private MaintenanceRecordService maintenanceRecordService;

    @PostMapping
    public ResponseEntity<ApiResponse<MaintenanceRecordResponse>> createOrUpdateRecord(@RequestBody MaintenanceRecordRequest request) {
        MaintenanceRecordResponse response = maintenanceRecordService.createOrUpdateRecord(request);
        return ResponseEntity.ok(new ApiResponse<>("200", "Maintenance record saved successfully", response));
    }

    @GetMapping("/inspection/{inspectionId}")
    public ResponseEntity<ApiResponse<MaintenanceRecordResponse>> getRecordByInspectionId(@PathVariable Long inspectionId) {
        MaintenanceRecordResponse response = maintenanceRecordService.getRecordByInspectionId(inspectionId);
        return ResponseEntity.ok(new ApiResponse<>("200", "Maintenance record retrieved successfully", response));
    }

    @GetMapping("/transformer/{transformerNo}")
    public ResponseEntity<ApiResponse<List<MaintenanceRecordResponse>>> getRecordsByTransformerNo(@PathVariable String transformerNo) {
        List<MaintenanceRecordResponse> response = maintenanceRecordService.getRecordsByTransformerNo(transformerNo);
        return ResponseEntity.ok(new ApiResponse<>("200", "Maintenance records retrieved successfully", response));
    }
}
