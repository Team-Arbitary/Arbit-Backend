package com.uom.Software_design_competition.application.controller;

import com.uom.Software_design_competition.domain.service.MaintenanceRecordService;
import com.uom.Software_design_competition.application.transport.request.MaintenanceRecordRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.MaintenanceRecordResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("${base-url.context}/api/maintenance-records")
public class MaintenanceRecordController {

    private final MaintenanceRecordService maintenanceRecordService;

    public MaintenanceRecordController(MaintenanceRecordService maintenanceRecordService) {
        this.maintenanceRecordService = maintenanceRecordService;
    }

    // Save a new maintenance record (creates new version)
    @PostMapping
    public ResponseEntity<ApiResponse<MaintenanceRecordResponse>> saveMaintenanceRecord(@RequestBody MaintenanceRecordRequest request) {
        try {
            ApiResponse<MaintenanceRecordResponse> response = maintenanceRecordService.saveMaintenanceRecord(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (BaseException ex) {
            log.error("Error saving maintenance record", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Also support /save endpoint for backward compatibility
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<MaintenanceRecordResponse>> saveMaintenanceRecordLegacy(@RequestBody MaintenanceRecordRequest request) {
        return saveMaintenanceRecord(request);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<MaintenanceRecordResponse>> updateMaintenanceRecord(@RequestBody MaintenanceRecordRequest request) {
        try {
            ApiResponse<MaintenanceRecordResponse> response = maintenanceRecordService.updateMaintenanceRecord(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException ex) {
            log.error("Error updating maintenance record", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaintenanceRecordResponse>> getMaintenanceRecordById(@PathVariable Long id) {
        try {
            ApiResponse<MaintenanceRecordResponse> response = maintenanceRecordService.getMaintenanceRecordById(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException ex) {
            log.error("Error fetching maintenance record", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Get current (latest) maintenance record for an inspection
    @GetMapping("/inspection/{inspectionId}")
    public ResponseEntity<ApiResponse<MaintenanceRecordResponse>> getCurrentMaintenanceRecord(
            @PathVariable Long inspectionId) {
        try {
            ApiResponse<MaintenanceRecordResponse> response =
                    maintenanceRecordService.getCurrentMaintenanceRecordByInspectionId(inspectionId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException ex) {
            log.error("Error fetching maintenance record", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Get all history versions for an inspection
    @GetMapping("/inspection/{inspectionId}/history")
    public ResponseEntity<ApiResponse<List<MaintenanceRecordResponse>>> getMaintenanceRecordHistory(
            @PathVariable Long inspectionId) {
        try {
            ApiResponse<List<MaintenanceRecordResponse>> response =
                    maintenanceRecordService.getMaintenanceRecordHistory(inspectionId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException ex) {
            log.error("Error fetching maintenance record history", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Get a specific version for an inspection
    @GetMapping("/inspection/{inspectionId}/version/{version}")
    public ResponseEntity<ApiResponse<MaintenanceRecordResponse>> getMaintenanceRecordByVersion(
            @PathVariable Long inspectionId, @PathVariable Integer version) {
        try {
            ApiResponse<MaintenanceRecordResponse> response =
                    maintenanceRecordService.getMaintenanceRecordByVersion(inspectionId, version);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException ex) {
            log.error("Error fetching maintenance record version", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Restore a specific version (make it current)
    @PostMapping("/inspection/{inspectionId}/restore/{version}")
    public ResponseEntity<ApiResponse<MaintenanceRecordResponse>> restoreVersion(
            @PathVariable Long inspectionId, @PathVariable Integer version) {
        try {
            ApiResponse<MaintenanceRecordResponse> response =
                    maintenanceRecordService.restoreVersion(inspectionId, version);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException ex) {
            log.error("Error restoring maintenance record version", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Get all current records for a transformer
    @GetMapping("/transformer/{transformerNo}")
    public ResponseEntity<ApiResponse<List<MaintenanceRecordResponse>>> getMaintenanceRecordsByTransformer(
            @PathVariable String transformerNo) {
        try {
            ApiResponse<List<MaintenanceRecordResponse>> response =
                    maintenanceRecordService.getMaintenanceRecordsByTransformerNo(transformerNo);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException ex) {
            log.error("Error fetching maintenance records", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMaintenanceRecord(@PathVariable Long id) {
        try {
            ApiResponse<Void> response = maintenanceRecordService.deleteMaintenanceRecord(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException ex) {
            log.error("Error deleting maintenance record", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }
}