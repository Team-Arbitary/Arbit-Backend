package com.uom.Software_design_competition.application.controller;

import com.uom.Software_design_competition.application.constant.LoggingAdviceConstants;
import com.uom.Software_design_competition.application.transport.request.ImageRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.ImageInspectResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.domain.service.ImageInspectionManagementService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${base-url.context}" + "/image-inspection-management")
@Slf4j
public class ImageInspectionManagementController extends BaseController {

    private final ImageInspectionManagementService imageInspectionManagementService;

    public ImageInspectionManagementController(ImageInspectionManagementService imageInspectionManagementService) {
        this.imageInspectionManagementService = imageInspectionManagementService;
    }

    // API 1: Upload Image (Baseline or Thermal)
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Void>> uploadImage(@ModelAttribute ImageRequest imageRequest,
                                                         HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<Void> resp = imageInspectionManagementService.uploadImage(imageRequest);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 2: Get Baseline Image by Transformer Number
    @GetMapping("/baseline/{transformerNo}")
    public ResponseEntity<ApiResponse<ImageInspectResponse>> getBaselineImage(@PathVariable String transformerNo,
                                                                              HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<ImageInspectResponse> resp = imageInspectionManagementService.getBaselineImageByTransformerNo(transformerNo);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 3: Update Baseline Image by Transformer Number
    @PutMapping("/baseline/{transformerNo}")
    public ResponseEntity<ApiResponse<Void>> updateBaselineImage(@PathVariable String transformerNo,
                                                                 @ModelAttribute ImageRequest imageRequest,
                                                                 HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<Void> resp = imageInspectionManagementService.updateBaselineImageByTransformerNo(transformerNo, imageRequest);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 4: Delete Baseline Image by Transformer Number
    @DeleteMapping("/baseline/{transformerNo}")
    public ResponseEntity<ApiResponse<Void>> deleteBaselineImage(@PathVariable String transformerNo,
                                                                 HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<Void> resp = imageInspectionManagementService.deleteBaselineImageByTransformerNo(transformerNo);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 5: Get Thermal Image by Inspection Number
    @GetMapping("/thermal/{inspectionNo}")
    public ResponseEntity<ApiResponse<ImageInspectResponse>> getThermalImage(@PathVariable String inspectionNo,
                                                                             HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<ImageInspectResponse> resp = imageInspectionManagementService.getThermalImageByInspectionNo(inspectionNo);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 6: Update Thermal Image by Inspection Number
    @PutMapping("/thermal/{inspectionNo}")
    public ResponseEntity<ApiResponse<Void>> updateThermalImage(@PathVariable String inspectionNo,
                                                                @ModelAttribute ImageRequest imageRequest,
                                                                HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<Void> resp = imageInspectionManagementService.updateThermalImageByInspectionNo(inspectionNo, imageRequest);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    // API 7: Delete Thermal Image by Inspection Number
    @DeleteMapping("/thermal/{inspectionNo}")
    public ResponseEntity<ApiResponse<Void>> deleteThermalImage(@PathVariable String inspectionNo,
                                                                HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<Void> resp = imageInspectionManagementService.deleteThermalImageByInspectionNo(inspectionNo);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }
}