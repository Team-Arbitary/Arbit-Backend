package com.uom.Software_design_competition.domain.service;

import com.uom.Software_design_competition.application.constant.LoggingAdviceConstants;
import com.uom.Software_design_competition.application.transport.request.ThermalInspectionReportRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.ThermalInspectionReportResponse;
import com.uom.Software_design_competition.application.util.exception.StackTraceTracker;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.application.util.resultenum.ResponseCodeEnum;
import com.uom.Software_design_competition.domain.entity.InspectionRecords;
import com.uom.Software_design_competition.domain.entity.ThermalInspectionReport;
import com.uom.Software_design_competition.domain.repository.InspectionRecordsRepository;
import com.uom.Software_design_competition.domain.repository.ThermalInspectionReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ThermalInspectionReportService {

    private final ThermalInspectionReportRepository thermalInspectionReportRepository;
    private final InspectionRecordsRepository inspectionRecordsRepository;

    public ThermalInspectionReportService(ThermalInspectionReportRepository thermalInspectionReportRepository,
                                           InspectionRecordsRepository inspectionRecordsRepository) {
        this.thermalInspectionReportRepository = thermalInspectionReportRepository;
        this.inspectionRecordsRepository = inspectionRecordsRepository;
    }

    @Transactional
    public ApiResponse<ThermalInspectionReportResponse> saveReport(ThermalInspectionReportRequest request) throws BaseException {
        try {
            InspectionRecords inspection = inspectionRecordsRepository.findById(request.getInspectionId())
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Inspection not found with ID: " + request.getInspectionId()));

            // Get the next version number
            Integer maxVersion = thermalInspectionReportRepository.findMaxVersionByInspectionId(request.getInspectionId());
            int newVersion = (maxVersion != null ? maxVersion : 0) + 1;

            // Mark all existing records for this inspection as not current
            thermalInspectionReportRepository.markAllAsNotCurrentByInspectionId(request.getInspectionId());

            // Create new record with new version
            ThermalInspectionReport record = ThermalInspectionReport.builder()
                    .inspection(inspection)
                    .transformerNo(request.getTransformerNo())
                    .reportData(request.getReportData())
                    .createdBy(request.getCreatedBy())
                    .version(newVersion)
                    .isCurrent(true)
                    .build();
            
            ThermalInspectionReport saved = thermalInspectionReportRepository.save(record);

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), "Thermal inspection report saved successfully", 
                    mapEntityToResponse(saved));
        } catch (BaseException be) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, be.getResponseCode(), be.getMessage(),
                    StackTraceTracker.displayStackStraceArray(be.getStackTrace()));
            throw be;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to save thermal inspection report: " + ex.getMessage());
        }
    }

    public ApiResponse<ThermalInspectionReportResponse> getCurrentReportByInspectionId(Long inspectionId) throws BaseException {
        try {
            ThermalInspectionReport record = thermalInspectionReportRepository.findByInspectionIdAndIsCurrentTrue(inspectionId)
                    .orElse(null);

            if (record == null) {
                return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), "No thermal inspection report found", null);
            }

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(),
                    mapEntityToResponse(record));
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to fetch thermal inspection report");
        }
    }

    public ApiResponse<List<ThermalInspectionReportResponse>> getReportHistory(Long inspectionId) throws BaseException {
        try {
            List<ThermalInspectionReport> records = thermalInspectionReportRepository.findByInspectionIdOrderByVersionDesc(inspectionId);

            List<ThermalInspectionReportResponse> responses = records.stream()
                    .map(this::mapEntityToResponse)
                    .collect(Collectors.toList());

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(), responses);
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to fetch thermal inspection report history");
        }
    }

    public ApiResponse<ThermalInspectionReportResponse> getReportByVersion(Long inspectionId, Integer version) throws BaseException {
        try {
            ThermalInspectionReport record = thermalInspectionReportRepository.findByInspectionIdAndVersion(inspectionId, version)
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Thermal inspection report version " + version + " not found for inspection: " + inspectionId));

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(),
                    mapEntityToResponse(record));
        } catch (BaseException be) {
            throw be;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to fetch thermal inspection report version");
        }
    }

    @Transactional
    public ApiResponse<ThermalInspectionReportResponse> restoreVersion(Long inspectionId, Integer version) throws BaseException {
        try {
            // Find the version to restore
            ThermalInspectionReport oldRecord = thermalInspectionReportRepository.findByInspectionIdAndVersion(inspectionId, version)
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Version " + version + " not found for inspection: " + inspectionId));

            // Get the next version number
            Integer maxVersion = thermalInspectionReportRepository.findMaxVersionByInspectionId(inspectionId);
            int newVersion = (maxVersion != null ? maxVersion : 0) + 1;

            // Mark all existing records as not current
            thermalInspectionReportRepository.markAllAsNotCurrentByInspectionId(inspectionId);

            // Create a new record as a copy of the old version
            ThermalInspectionReport newRecord = ThermalInspectionReport.builder()
                    .inspection(oldRecord.getInspection())
                    .transformerNo(oldRecord.getTransformerNo())
                    .reportData(oldRecord.getReportData())
                    .createdBy(oldRecord.getCreatedBy())
                    .version(newVersion)
                    .isCurrent(true)
                    .build();

            ThermalInspectionReport saved = thermalInspectionReportRepository.save(newRecord);

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), 
                    "Version " + version + " restored as version " + newVersion,
                    mapEntityToResponse(saved));
        } catch (BaseException be) {
            throw be;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to restore thermal inspection report version");
        }
    }

    public ApiResponse<List<ThermalInspectionReportResponse>> getReportsByTransformerNo(String transformerNo) throws BaseException {
        try {
            List<ThermalInspectionReport> records = thermalInspectionReportRepository
                    .findByTransformerNoAndIsCurrentTrueOrderByCreatedAtDesc(transformerNo);

            List<ThermalInspectionReportResponse> responses = records.stream()
                    .map(this::mapEntityToResponse)
                    .collect(Collectors.toList());

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(), responses);
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to fetch thermal inspection reports");
        }
    }

    public ApiResponse<Void> deleteReport(Long id) throws BaseException {
        try {
            ThermalInspectionReport entity = thermalInspectionReportRepository.findById(id)
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Thermal inspection report not found with ID: " + id));

            thermalInspectionReportRepository.delete(entity);
            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message());
        } catch (BaseException be) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, be.getResponseCode(), be.getMessage(),
                    StackTraceTracker.displayStackStraceArray(be.getStackTrace()));
            throw be;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis(), ex.getMessage(),
                    StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to delete thermal inspection report");
        }
    }

    private ThermalInspectionReportResponse mapEntityToResponse(ThermalInspectionReport entity) {
        return ThermalInspectionReportResponse.builder()
                .id(entity.getId())
                .inspectionId(entity.getInspection() != null ? entity.getInspection().getId() : null)
                .transformerNo(entity.getTransformerNo())
                .reportData(entity.getReportData())
                .version(entity.getVersion())
                .isCurrent(entity.getIsCurrent())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
