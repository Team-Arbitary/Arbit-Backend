package com.uom.Software_design_competition.application.controller;

import com.uom.Software_design_competition.application.constant.LoggingAdviceConstants;
import com.uom.Software_design_competition.application.transport.request.FilterRequest;
import com.uom.Software_design_competition.application.transport.request.InspectionRecordsRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.InspectionRecordsResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.domain.entity.InspectionRecords;
import com.uom.Software_design_competition.domain.service.InspectionManagementService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${base-url.context}" + "/inspection-management")
@Slf4j
public class InspectionManagementController extends BaseController {

    private final InspectionManagementService inspectionManagementService;

    public InspectionManagementController(InspectionManagementService inspectionManagementService) {
        this.inspectionManagementService = inspectionManagementService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> addInspectionRecord(@RequestBody InspectionRecordsRequest inspectionRecordsRequest,
                                                                 HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<Void> resp = inspectionManagementService.saveRecord(inspectionRecordsRequest);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<ApiResponse<InspectionRecords>> getInspectionById(@PathVariable Long id,
                                                                            HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<InspectionRecords> resp = inspectionManagementService.getInspectionById(id);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }
    @GetMapping("/transformer/{transformerNo}")
    public ResponseEntity<ApiResponse<List<InspectionRecordsResponse>>> getInspectionsByTransformerNo(
            @PathVariable String transformerNo,
            HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<List<InspectionRecordsResponse>> resp = inspectionManagementService.getInspectionsByTransformerNo(transformerNo);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    @GetMapping("/view-all")
    public ResponseEntity<ApiResponse<List<InspectionRecordsResponse>>> getAllInspections(HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<List<InspectionRecordsResponse>> resp = inspectionManagementService.getAllInspections();
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<InspectionRecords>> updateInspection(@RequestBody InspectionRecords inspectionRecords,
                                                                           HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<InspectionRecords> resp = inspectionManagementService.updateInspection(inspectionRecords);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInspection(@PathVariable Long id, HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<Void> resp = inspectionManagementService.deleteInspectionById(id);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    @PostMapping("/filter")
    public ResponseEntity<ApiResponse<List<InspectionRecords>>> filterInspectionRecords(@RequestBody FilterRequest filterRequest,
                                                                                        HttpServletRequest httpServletRequest) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        ApiResponse<List<InspectionRecords>> resp = inspectionManagementService.filterRecords(filterRequest);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

}