package com.uom.Software_design_competition.application.controller;

import com.uom.Software_design_competition.application.constant.LoggingAdviceConstants;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.domain.entity.AnalysisResult;
import com.uom.Software_design_competition.domain.service.ImageAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/image-analysis")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:3000", "http://127.0.0.1:8080",
        "https://arbit-frontend.vercel.app" }, allowCredentials = "true")
@Slf4j
@Tag(name = "Image Analysis", description = "AI-powered thermal image analysis APIs for transformer inspection")
public class ImageAnalysisController extends BaseController {

    private final ImageAnalysisService imageAnalysisService;

    public ImageAnalysisController(ImageAnalysisService imageAnalysisService) {
        this.imageAnalysisService = imageAnalysisService;
    }

    // API 1: Trigger Manual Analysis
    @Operation(
        summary = "Trigger manual thermal analysis",
        description = "Initiates AI-powered thermal image analysis for a specific inspection and transformer. Requires both baseline and thermal images to be uploaded first."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analysis triggered successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or missing images"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/analyze/{inspectionNo}/{transformerNo}")
    public ResponseEntity<ApiResponse<AnalysisResult>> triggerAnalysis(
            @Parameter(description = "Inspection number", example = "6") @PathVariable String inspectionNo,
            @Parameter(description = "Transformer number", example = "TX-1345") @PathVariable String transformerNo,
            HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<AnalysisResult> resp = imageAnalysisService.performAnalysis(inspectionNo, transformerNo);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime,
                resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 1b: Simplified Manual Analysis (Get transformer from inspection)
    @Operation(
        summary = "Trigger manual analysis (simplified)",
        description = "Initiates analysis using only inspection number. Automatically retrieves transformer number from inspection record."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analysis triggered successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Inspection not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/analyze/{inspectionNo}")
    public ResponseEntity<ApiResponse<AnalysisResult>> triggerAnalysisSimple(
            @Parameter(description = "Inspection number", example = "6") @PathVariable String inspectionNo,
            HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<AnalysisResult> resp = imageAnalysisService.performAnalysisWithInspectionNo(inspectionNo);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime,
                resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 2: Get Analysis Result by Inspection Number
    @Operation(
        summary = "Get analysis result",
        description = "Retrieves thermal image analysis results for a specific inspection including anomaly detection data and thermal overlay image."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analysis result retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "No analysis result found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/result/{inspectionNo}")
    public ResponseEntity<ApiResponse<AnalysisResult>> getAnalysisResult(
            @Parameter(description = "Inspection number", example = "6") @PathVariable String inspectionNo,
            HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<AnalysisResult> resp = imageAnalysisService.getAnalysisResult(inspectionNo);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime,
                resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 6: Update Analysis Result JSON
    @Operation(
        summary = "Update analysis result JSON",
        description = "Updates the analysis result JSON after frontend modifications. Used to save user-edited anomaly states or analysis data."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analysis result updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or record not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/result/update/{inspectionNo}/{transformerNo}")
    public ResponseEntity<ApiResponse<AnalysisResult>> updateAnalysisResultJson(
            @Parameter(description = "Inspection number", example = "6") @PathVariable String inspectionNo,
            @Parameter(description = "Transformer number", example = "TX-1345") @PathVariable String transformerNo,
            @Parameter(description = "Updated analysis result JSON", example = "[{\"id\":\"1\",\"anomalyState\":\"Faulty\"}]")
            @RequestBody String analysisResultJson,
            HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        log.info("Received update request for inspection: {}, transformer: {}", inspectionNo, transformerNo);
        ApiResponse<AnalysisResult> resp = imageAnalysisService.updateAnalysisResultJson(inspectionNo, transformerNo,
                analysisResultJson);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime,
                resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 3: Get Analysis Results by Transformer Number
    @Operation(
        summary = "Get analysis results by transformer",
        description = "Retrieves all analysis results for a specific transformer across multiple inspections."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analysis results retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/results/transformer/{transformerNo}")
    public ResponseEntity<ApiResponse<List<AnalysisResult>>> getAnalysisResultsByTransformer(
            @Parameter(description = "Transformer number", example = "TX-1345") @PathVariable String transformerNo,
            HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<List<AnalysisResult>> resp = imageAnalysisService.getAnalysisResultsByTransformer(transformerNo);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime,
                resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 4: Check and Update Status
    @PostMapping("/check-status/{inspectionNo}/{transformerNo}")
    public ResponseEntity<ApiResponse<Void>> checkAndUpdateStatus(@PathVariable String inspectionNo,
            @PathVariable String transformerNo,
            HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        imageAnalysisService.checkAndTriggerAnalysis(inspectionNo, transformerNo);
        ApiResponse<Void> resp = new ApiResponse<>("200", "Status check completed");
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime,
                resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 5: Test endpoint to directly update inspection status (for testing ID
    // fallback logic)
    @PostMapping("/test-update-status/{inspectionNo}")
    public ResponseEntity<ApiResponse<String>> testUpdateStatus(@PathVariable String inspectionNo,
            HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        String result = imageAnalysisService.testStatusUpdate(inspectionNo);
        ApiResponse<String> resp = new ApiResponse<>("200", "Status update test completed", result);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime,
                resp.getResponseDescription());
        return setResponseEntity(resp);
    }
}