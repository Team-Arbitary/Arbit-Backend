package com.uom.Software_design_competition.application.controller;

import com.uom.Software_design_competition.application.constant.LoggingAdviceConstants;
import com.uom.Software_design_competition.application.transport.request.FilterRequest;
import com.uom.Software_design_competition.application.transport.request.TransformerRecordsRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.TransformerRecordsResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;

import com.uom.Software_design_competition.domain.entity.TransformerRecords;
import com.uom.Software_design_competition.domain.service.TransformerManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${base-url.context}" + "/transformer-management")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000", "http://127.0.0.1:8080"}, 
             allowCredentials = "true")
@Slf4j
public class TransformerManagementController extends BaseController {

    private final TransformerManagementService transformerManagementService;

    public TransformerManagementController(TransformerManagementService transformerManagementService) {
        this.transformerManagementService = transformerManagementService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> addTransformerRecord(@Valid @RequestBody TransformerRecordsRequest transformerRecordsRequest, HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<Void> resp = transformerManagementService.saveRecord(transformerRecordsRequest);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<ApiResponse<TransformerRecords>> getTransformerById(@PathVariable Long id, HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<TransformerRecords> resp = transformerManagementService.getTransformerById(id);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    @GetMapping("/view-all")
    public ResponseEntity<ApiResponse<List<TransformerRecordsResponse>>> getAllTransformers(HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<List<TransformerRecordsResponse>> resp = transformerManagementService.getAllTransformers();
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<TransformerRecords>> updateTransformer(@RequestBody TransformerRecords transformerRecords, HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<TransformerRecords> resp = transformerManagementService.updateTransformer(transformerRecords);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransformer(@PathVariable Long id, HttpServletRequest request) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, request.getMethod(), request.getRequestURI());
        ApiResponse<Void> resp = transformerManagementService.deleteTransformerById(id);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return setResponseEntity(resp);
    }

    @PostMapping("/filter")
    public ResponseEntity<ApiResponse<List<TransformerRecords>>> filterTransformerRecords(@RequestBody FilterRequest filterRequest, HttpServletRequest httpServletRequest) throws BaseException {
        long startTime = System.currentTimeMillis();
        log.info(LoggingAdviceConstants.REQUEST_INITIATED, httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        ApiResponse<List<TransformerRecords>> resp = transformerManagementService.filterRecords(filterRequest);
        log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, resp.getResponseDescription());
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

}