package com.uom.Software_design_competition.application.controller;

import com.uom.Software_design_competition.application.constant.LoggingAdviceConstants;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.domain.entity.AnalysisResult;
import com.uom.Software_design_competition.domain.service.ImageAnalysisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${base-url.context}" + "/image-analysis")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000", "http://127.0.0.1:8080", "https://arbit-frontend.vercel.app"}, 
             allowCredentials = "true")
@Slf4j
public class ImageAnalysisController extends BaseController {

    private final ImageAnalysisService imageAnalysisService;

    public ImageAnalysisController(ImageAnalysisService imageAnalysisService) {
        this.imageAnalysisService = imageAnalysisService;
    }

    // API 1: Trigger Manual Analysis
    @PostMapping("/analyze/{inspectionNo}/{transformerNo}")
    public ResponseEntity<ApiResponse<AnalysisResult>> triggerAnalysis(@PathVariable String inspectionNo,
                                                                       @PathVariable String transformerNo,
                                                                       HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<AnalysisResult> resp = imageAnalysisService.performAnalysis(inspectionNo, transformerNo);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 1b: Simplified Manual Analysis (Get transformer from inspection)
    @PostMapping("/analyze/{inspectionNo}")
    public ResponseEntity<ApiResponse<AnalysisResult>> triggerAnalysisSimple(@PathVariable String inspectionNo,
                                                                           HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<AnalysisResult> resp = imageAnalysisService.performAnalysisWithInspectionNo(inspectionNo);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 2: Get Analysis Result by Inspection Number
    @GetMapping("/result/{inspectionNo}")
    public ResponseEntity<ApiResponse<AnalysisResult>> getAnalysisResult(@PathVariable String inspectionNo,
                                                                         HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<AnalysisResult> resp = imageAnalysisService.getAnalysisResult(inspectionNo);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 3: Get Analysis Results by Transformer Number
    @GetMapping("/results/transformer/{transformerNo}")
    public ResponseEntity<ApiResponse<List<AnalysisResult>>> getAnalysisResultsByTransformer(@PathVariable String transformerNo,
                                                                                             HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<List<AnalysisResult>> resp = imageAnalysisService.getAnalysisResultsByTransformer(transformerNo);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
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
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 5: Test endpoint to directly update inspection status (for testing ID fallback logic)
    @PostMapping("/test-update-status/{inspectionNo}")
    public ResponseEntity<ApiResponse<String>> testUpdateStatus(@PathVariable String inspectionNo,
                                                                HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        String result = imageAnalysisService.testStatusUpdate(inspectionNo);
        ApiResponse<String> resp = new ApiResponse<>("200", "Status update test completed", result);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }
}