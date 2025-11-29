package com.uom.Software_design_competition.application.controller;

import com.uom.Software_design_competition.domain.service.ThermalInspectionReportService;
import com.uom.Software_design_competition.application.transport.request.ThermalInspectionReportRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.ThermalInspectionReportResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("${base-url.context}/api/thermal-inspection-reports")
public class ThermalInspectionReportController {

    private final ThermalInspectionReportService thermalInspectionReportService;

    public ThermalInspectionReportController(ThermalInspectionReportService thermalInspectionReportService) {
        this.thermalInspectionReportService = thermalInspectionReportService;
    }

    // Save a new thermal inspection report (creates new version)
    @PostMapping
    public ResponseEntity<ApiResponse<ThermalInspectionReportResponse>> saveReport(@RequestBody ThermalInspectionReportRequest request) {
        try {
            ApiResponse<ThermalInspectionReportResponse> response = thermalInspectionReportService.saveReport(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (BaseException ex) {
            log.error("Error saving thermal inspection report", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Get current (latest) thermal inspection report for an inspection
    @GetMapping("/inspection/{inspectionId}")
    public ResponseEntity<ApiResponse<ThermalInspectionReportResponse>> getCurrentReport(
            @PathVariable Long inspectionId) {
        try {
            ApiResponse<ThermalInspectionReportResponse> response =
                    thermalInspectionReportService.getCurrentReportByInspectionId(inspectionId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException ex) {
            log.error("Error fetching thermal inspection report", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Get all history versions for an inspection
    @GetMapping("/inspection/{inspectionId}/history")
    public ResponseEntity<ApiResponse<List<ThermalInspectionReportResponse>>> getReportHistory(
            @PathVariable Long inspectionId) {
        try {
            ApiResponse<List<ThermalInspectionReportResponse>> response =
                    thermalInspectionReportService.getReportHistory(inspectionId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException ex) {
            log.error("Error fetching thermal inspection report history", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Get a specific version for an inspection
    @GetMapping("/inspection/{inspectionId}/version/{version}")
    public ResponseEntity<ApiResponse<ThermalInspectionReportResponse>> getReportByVersion(
            @PathVariable Long inspectionId, @PathVariable Integer version) {
        try {
            ApiResponse<ThermalInspectionReportResponse> response =
                    thermalInspectionReportService.getReportByVersion(inspectionId, version);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException ex) {
            log.error("Error fetching thermal inspection report version", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Restore a specific version (make it current)
    @PostMapping("/inspection/{inspectionId}/restore/{version}")
    public ResponseEntity<ApiResponse<ThermalInspectionReportResponse>> restoreVersion(
            @PathVariable Long inspectionId, @PathVariable Integer version) {
        try {
            ApiResponse<ThermalInspectionReportResponse> response =
                    thermalInspectionReportService.restoreVersion(inspectionId, version);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException ex) {
            log.error("Error restoring thermal inspection report version", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Get all current reports for a transformer
    @GetMapping("/transformer/{transformerNo}")
    public ResponseEntity<ApiResponse<List<ThermalInspectionReportResponse>>> getReportsByTransformer(
            @PathVariable String transformerNo) {
        try {
            ApiResponse<List<ThermalInspectionReportResponse>> response =
                    thermalInspectionReportService.getReportsByTransformerNo(transformerNo);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException ex) {
            log.error("Error fetching thermal inspection reports", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(@PathVariable Long id) {
        try {
            ApiResponse<Void> response = thermalInspectionReportService.deleteReport(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException ex) {
            log.error("Error deleting thermal inspection report", ex);
            return new ResponseEntity<>(new ApiResponse<>(ex.getResponseCode(), ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
