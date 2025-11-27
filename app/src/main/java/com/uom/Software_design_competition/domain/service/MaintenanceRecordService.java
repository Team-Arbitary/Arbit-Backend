package com.uom.Software_design_competition.domain.service;

import com.uom.Software_design_competition.application.constant.LoggingAdviceConstants;
import com.uom.Software_design_competition.application.transport.request.MaintenanceRecordRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.MaintenanceRecordResponse;
import com.uom.Software_design_competition.application.util.exception.StackTraceTracker;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.application.util.resultenum.ResponseCodeEnum;
import com.uom.Software_design_competition.domain.entity.InspectionRecords;
import com.uom.Software_design_competition.domain.entity.MaintenanceRecord;
import com.uom.Software_design_competition.domain.repository.InspectionRecordsRepository;
import com.uom.Software_design_competition.domain.repository.MaintenanceRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MaintenanceRecordService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final InspectionRecordsRepository inspectionRecordsRepository;

    public MaintenanceRecordService(MaintenanceRecordRepository maintenanceRecordRepository,
                                        InspectionRecordsRepository inspectionRecordsRepository) {
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.inspectionRecordsRepository = inspectionRecordsRepository;
    }

    @Transactional
    public ApiResponse<Void> saveMaintenanceRecord(MaintenanceRecordRequest request) throws BaseException {
        try {
            InspectionRecords inspection = inspectionRecordsRepository.findById(request.getInspectionId())
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Inspection not found with ID: " + request.getInspectionId()));

            MaintenanceRecord record = maintenanceRecordRepository.findByInspectionId(request.getInspectionId())
                    .orElse(new MaintenanceRecord());

            record.setInspection(inspection);
            mapRequestToEntity(request, record);
            maintenanceRecordRepository.save(record);

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message());
        } catch (BaseException be) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, be.getResponseCode(), be.getMessage(),
                    StackTraceTracker.displayStackStraceArray(be.getStackTrace()));
            throw be;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to save maintenance record");
        }
    }

    @Transactional
    public ApiResponse<Void> updateMaintenanceRecord(MaintenanceRecordRequest request) throws BaseException {
        try {
            if (request == null || request.getId() == null) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(), "Empty or invalid request");
            }
            MaintenanceRecord entity = maintenanceRecordRepository.findById(request.getId())
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Maintenance record not found with ID: " + request.getId()));

            mapRequestToEntity(request, entity);
            entity.setUpdatedAt(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
            maintenanceRecordRepository.save(entity);

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message());
        } catch (BaseException be) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, be.getResponseCode(), be.getMessage(),
                    StackTraceTracker.displayStackStraceArray(be.getStackTrace()));
            throw be;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to update maintenance record");
        }
    }

    public ApiResponse<MaintenanceRecordResponse> getMaintenanceRecordById(Long id) throws BaseException {
        try {
            MaintenanceRecord entity = maintenanceRecordRepository.findById(id)
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Maintenance record not found with ID: " + id));

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(),
                    mapEntityToResponse(entity));
        } catch (BaseException be) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, be.getResponseCode(), be.getMessage(),
                    StackTraceTracker.displayStackStraceArray(be.getStackTrace()));
            throw be;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to fetch maintenance record");
        }
    }

    public ApiResponse<List<MaintenanceRecordResponse>> getMaintenanceRecordsByInspectionId(Long inspectionId) throws BaseException {
        try {
            List<MaintenanceRecord> records = maintenanceRecordRepository.findByInspectionId(inspectionId)
                    .map(List::of)
                    .orElseGet(List::of);

            List<MaintenanceRecordResponse> responses = records.stream()
                    .map(this::mapEntityToResponse)
                    .collect(Collectors.toList());

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(), responses);
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to fetch maintenance records");
        }
    }

    public ApiResponse<Void> deleteMaintenanceRecord(Long id) throws BaseException {
        try {
            MaintenanceRecord entity = maintenanceRecordRepository.findById(id)
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Maintenance record not found with ID: " + id));

            maintenanceRecordRepository.delete(entity);
            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message());
        } catch (BaseException be) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, be.getResponseCode(), be.getMessage(),
                    StackTraceTracker.displayStackStraceArray(be.getStackTrace()));
            throw be;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to delete maintenance record");
        }
    }

    /* ------------------ Helpers ------------------ */
    private void mapRequestToEntity(MaintenanceRecordRequest request, MaintenanceRecord entity) {
        if (request == null || entity == null) return;
        entity.setStartTime(request.getStartTime());
        entity.setCompletionTime(request.getCompletionTime());
        entity.setSupervisedBy(request.getSupervisedBy());
        entity.setTechI(request.getTechI());
        entity.setTechII(request.getTechII());
        entity.setTechIII(request.getTechIII());
        entity.setHelpers(request.getHelpers());
        entity.setInspectedBy(request.getInspectedBy());
        entity.setInspectedByDate(request.getInspectedByDate());
        entity.setRectifiedBy(request.getRectifiedBy());
        entity.setRectifiedByDate(request.getRectifiedByDate());
        entity.setReInspectedBy(request.getReInspectedBy());
        entity.setReInspectedByDate(request.getReInspectedByDate());
        entity.setCss(request.getCss());
        entity.setCssDate(request.getCssDate());
    }

    private MaintenanceRecordResponse mapEntityToResponse(MaintenanceRecord entity) {
        return MaintenanceRecordResponse.builder()
                .id(entity.getId())
                .inspectionId(entity.getInspection() != null ? entity.getInspection().getId() : null)
                .transformerNo(entity.getInspection() != null ? entity.getInspection().getTransformerNo() : null)
                .startTime(entity.getStartTime())
                .completionTime(entity.getCompletionTime())
                .supervisedBy(entity.getSupervisedBy())
                .techI(entity.getTechI())
                .techII(entity.getTechII())
                .techIII(entity.getTechIII())
                .helpers(entity.getHelpers())
                .inspectedBy(entity.getInspectedBy())
                .inspectedByDate(entity.getInspectedByDate())
                .rectifiedBy(entity.getRectifiedBy())
                .rectifiedByDate(entity.getRectifiedByDate())
                .reInspectedBy(entity.getReInspectedBy())
                .reInspectedByDate(entity.getReInspectedByDate())
                .css(entity.getCss())
                .cssDate(entity.getCssDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}