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
    public ApiResponse<MaintenanceRecordResponse> saveMaintenanceRecord(MaintenanceRecordRequest request) throws BaseException {
        try {
            InspectionRecords inspection = inspectionRecordsRepository.findById(request.getInspectionId())
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Inspection not found with ID: " + request.getInspectionId()));

            // Get the next version number
            Integer maxVersion = maintenanceRecordRepository.findMaxVersionByInspectionId(request.getInspectionId());
            int newVersion = (maxVersion != null ? maxVersion : 0) + 1;

            // Mark all existing records for this inspection as not current
            maintenanceRecordRepository.markAllAsNotCurrentByInspectionId(request.getInspectionId());

            // Create new record with new version
            MaintenanceRecord record = new MaintenanceRecord();
            record.setInspection(inspection);
            record.setVersion(newVersion);
            record.setIsCurrent(true);
            mapRequestToEntity(request, record);
            
            MaintenanceRecord saved = maintenanceRecordRepository.save(record);

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), "Maintenance record saved successfully", 
                    mapEntityToResponse(saved));
        } catch (BaseException be) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, be.getResponseCode(), be.getMessage(),
                    StackTraceTracker.displayStackStraceArray(be.getStackTrace()));
            throw be;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to save maintenance record: " + ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<MaintenanceRecordResponse> updateMaintenanceRecord(MaintenanceRecordRequest request) throws BaseException {
        try {
            if (request == null || request.getId() == null) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(), "Empty or invalid request");
            }
            MaintenanceRecord entity = maintenanceRecordRepository.findById(request.getId())
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Maintenance record not found with ID: " + request.getId()));

            mapRequestToEntity(request, entity);
            entity.setUpdatedAt(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
            MaintenanceRecord saved = maintenanceRecordRepository.save(entity);

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), "Maintenance record updated successfully",
                    mapEntityToResponse(saved));
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

    public ApiResponse<MaintenanceRecordResponse> getCurrentMaintenanceRecordByInspectionId(Long inspectionId) throws BaseException {
        try {
            MaintenanceRecord record = maintenanceRecordRepository.findByInspectionIdAndIsCurrentTrue(inspectionId)
                    .orElse(null);

            if (record == null) {
                return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), "No maintenance record found", null);
            }

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(),
                    mapEntityToResponse(record));
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to fetch maintenance record");
        }
    }

    public ApiResponse<List<MaintenanceRecordResponse>> getMaintenanceRecordHistory(Long inspectionId) throws BaseException {
        try {
            List<MaintenanceRecord> records = maintenanceRecordRepository.findByInspectionIdOrderByVersionDesc(inspectionId);

            List<MaintenanceRecordResponse> responses = records.stream()
                    .map(this::mapEntityToResponse)
                    .collect(Collectors.toList());

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(), responses);
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to fetch maintenance record history");
        }
    }

    public ApiResponse<MaintenanceRecordResponse> getMaintenanceRecordByVersion(Long inspectionId, Integer version) throws BaseException {
        try {
            MaintenanceRecord record = maintenanceRecordRepository.findByInspectionIdAndVersion(inspectionId, version)
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Maintenance record version " + version + " not found for inspection: " + inspectionId));

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(),
                    mapEntityToResponse(record));
        } catch (BaseException be) {
            throw be;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to fetch maintenance record version");
        }
    }

    @Transactional
    public ApiResponse<MaintenanceRecordResponse> restoreVersion(Long inspectionId, Integer version) throws BaseException {
        try {
            // Find the version to restore
            MaintenanceRecord oldRecord = maintenanceRecordRepository.findByInspectionIdAndVersion(inspectionId, version)
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Version " + version + " not found for inspection: " + inspectionId));

            // Get the next version number
            Integer maxVersion = maintenanceRecordRepository.findMaxVersionByInspectionId(inspectionId);
            int newVersion = (maxVersion != null ? maxVersion : 0) + 1;

            // Mark all existing records as not current
            maintenanceRecordRepository.markAllAsNotCurrentByInspectionId(inspectionId);

            // Create a new record as a copy of the old version
            MaintenanceRecord newRecord = MaintenanceRecord.builder()
                    .inspection(oldRecord.getInspection())
                    .inspectorName(oldRecord.getInspectorName())
                    .status(oldRecord.getStatus())
                    .voltageReading(oldRecord.getVoltageReading())
                    .currentReading(oldRecord.getCurrentReading())
                    .recommendedAction(oldRecord.getRecommendedAction())
                    .remarks(oldRecord.getRemarks())
                    .reportData(oldRecord.getReportData())
                    .startTime(oldRecord.getStartTime())
                    .completionTime(oldRecord.getCompletionTime())
                    .supervisedBy(oldRecord.getSupervisedBy())
                    .techI(oldRecord.getTechI())
                    .techII(oldRecord.getTechII())
                    .techIII(oldRecord.getTechIII())
                    .helpers(oldRecord.getHelpers())
                    .inspectedBy(oldRecord.getInspectedBy())
                    .inspectedByDate(oldRecord.getInspectedByDate())
                    .rectifiedBy(oldRecord.getRectifiedBy())
                    .rectifiedByDate(oldRecord.getRectifiedByDate())
                    .reInspectedBy(oldRecord.getReInspectedBy())
                    .reInspectedByDate(oldRecord.getReInspectedByDate())
                    .css(oldRecord.getCss())
                    .cssDate(oldRecord.getCssDate())
                    .version(newVersion)
                    .isCurrent(true)
                    .build();

            MaintenanceRecord saved = maintenanceRecordRepository.save(newRecord);

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), 
                    "Version " + version + " restored as version " + newVersion,
                    mapEntityToResponse(saved));
        } catch (BaseException be) {
            throw be;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to restore maintenance record version");
        }
    }

    public ApiResponse<List<MaintenanceRecordResponse>> getMaintenanceRecordsByTransformerNo(String transformerNo) throws BaseException {
        try {
            List<MaintenanceRecord> records = maintenanceRecordRepository
                    .findByInspectionTransformerNoAndIsCurrentTrueOrderByCreatedAtDesc(transformerNo);

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

    public ApiResponse<List<MaintenanceRecordResponse>> getMaintenanceRecordsByInspectionId(Long inspectionId) throws BaseException {
        try {
            List<MaintenanceRecord> records = maintenanceRecordRepository.findByInspectionIdOrderByVersionDesc(inspectionId);

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
        
        // New fields
        entity.setInspectorName(request.getInspectorName());
        entity.setStatus(request.getStatus());
        entity.setVoltageReading(request.getVoltageReading());
        entity.setCurrentReading(request.getCurrentReading());
        entity.setRecommendedAction(request.getRecommendedAction());
        entity.setRemarks(request.getRemarks());
        entity.setReportData(request.getReportData());
        
        // Legacy fields
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
                .inspectorName(entity.getInspectorName())
                .status(entity.getStatus())
                .voltageReading(entity.getVoltageReading())
                .currentReading(entity.getCurrentReading())
                .recommendedAction(entity.getRecommendedAction())
                .remarks(entity.getRemarks())
                .reportData(entity.getReportData())
                .version(entity.getVersion())
                .isCurrent(entity.getIsCurrent())
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