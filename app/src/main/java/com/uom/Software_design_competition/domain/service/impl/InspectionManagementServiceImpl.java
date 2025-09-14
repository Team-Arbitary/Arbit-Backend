package com.uom.Software_design_competition.domain.service.impl;

import com.uom.Software_design_competition.application.constant.LoggingAdviceConstants;
import com.uom.Software_design_competition.application.transport.request.FilterRequest;
import com.uom.Software_design_competition.application.transport.request.InspectionRecordsRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.PageDetail;
import com.uom.Software_design_competition.application.transport.response.InspectionRecordsResponse;
import com.uom.Software_design_competition.application.util.exception.StackTraceTracker;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.application.util.resultenum.ResponseCodeEnum;
import com.uom.Software_design_competition.domain.entity.ImageInspect;
import com.uom.Software_design_competition.domain.entity.InspectionRecords;
import com.uom.Software_design_competition.domain.mapper.InspectionRecordsMapper;
import com.uom.Software_design_competition.domain.repository.ImageInspectRepository;
import com.uom.Software_design_competition.domain.repository.InspectionRecordsRepository;
import com.uom.Software_design_competition.domain.service.InspectionManagementService;
import com.uom.Software_design_competition.domain.util.QueryBuilder;

import com.uom.Software_design_competition.domain.util.SequenceGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InspectionManagementServiceImpl implements InspectionManagementService {

    private final InspectionRecordsRepository inspectionRecordsRepository;
    private final InspectionRecordsMapper inspectionRecordsMapper;
    private final QueryBuilder queryBuilder;
    private final ImageInspectRepository imageInspectRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    public InspectionManagementServiceImpl(InspectionRecordsRepository inspectionRecordsRepository,
                                           InspectionRecordsMapper inspectionRecordsMapper,
                                           QueryBuilder queryBuilder,
                                           ImageInspectRepository imageInspectRepository) {
        this.inspectionRecordsRepository = inspectionRecordsRepository;
        this.inspectionRecordsMapper = inspectionRecordsMapper;
        this.queryBuilder = queryBuilder;
        this.imageInspectRepository = imageInspectRepository;
    }

    @Override
    public ApiResponse<Void> saveRecord(InspectionRecordsRequest inspectionRecordsRequest) throws BaseException {
        long startTime = System.currentTimeMillis();

        if (inspectionRecordsRequest.getBranch() == null ||
                inspectionRecordsRequest.getTransformerNo() == null ||
                inspectionRecordsRequest.getDateOfInspection() == null ||
                inspectionRecordsRequest.getTime() == null) {
            return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST_INVALID_FIELDS.code(),
                    ResponseCodeEnum.BAD_REQUEST_INVALID_FIELDS.message());
        }

        try {
            log.info("Creating inspection record: {}", inspectionRecordsRequest);

            // Check if inspection record already exists for same transformer, date and time
//            if (inspectionRecordsRepository.existsByTransformerNoAndDateOfInspectionAndTime(
//                    inspectionRecordsRequest.getTransformerNo(),
//                    inspectionRecordsRequest.getDateOfInspection(),
//                    inspectionRecordsRequest.getTime())) {
//                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
//                        "Inspection record already exists for this transformer at the specified date and time");
//            }

            // Generate inspection number
            long seq = sequenceGeneratorService.generateSequence("inspection_sequence");

            InspectionRecords inspectionRecords = inspectionRecordsMapper.mapRequestToEntityCreate(inspectionRecordsRequest);
            inspectionRecords.setInspectionNo( String.format("%06d", seq));

            inspectionRecordsRepository.save(inspectionRecords);
            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message());
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - startTime,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.LOG_NOT_CONNECTED.code(), ResponseCodeEnum.LOG_NOT_CONNECTED.message());
        }
    }

    @Override
    public ApiResponse<List<InspectionRecordsResponse>> getInspectionsByTransformerNo(String transformerNo) throws BaseException {
        long start = System.currentTimeMillis();
        try {
            log.info("Fetching inspections for transformer: {}", transformerNo);

            List<InspectionRecords> inspectionEntities = inspectionRecordsRepository
                    .findByTransformerNoOrderByDateOfInspectionDesc(transformerNo);

            List<InspectionRecordsResponse> responseList = new ArrayList<>();
            for (InspectionRecords entity : inspectionEntities) {
                // Get status directly from image_inspect table
                String directStatus = getStatusFromImageInspect(entity.getInspectionNo());
                entity.setStatus(directStatus);
                
                responseList.add(inspectionRecordsMapper.mapEntityToResponse(entity));
            }

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(), responseList);
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to retrieve inspection records for transformer: " + transformerNo);
        }
    }

    @Override
    public ApiResponse<InspectionRecords> getInspectionById(Long id) throws BaseException {
        long start = System.currentTimeMillis();
        try {
            InspectionRecords inspectionRecord = inspectionRecordsRepository.findById(id)
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Inspection record not found with ID: " + id));

            // Get status directly from image_inspect table
            String directStatus = getStatusFromImageInspect(inspectionRecord.getInspectionNo());
            inspectionRecord.setStatus(directStatus);

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(), inspectionRecord);
        } catch (BaseException ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw ex;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(), "Failed to retrieve inspection record");
        }
    }

    /**
     * Get status directly from image_inspect table for the given inspection
     */
    private String getStatusFromImageInspect(String inspectionNo) {
        try {
            // Find any image_inspect record for this inspection and get its status
            List<ImageInspect> images = imageInspectRepository.findByInspectionNoOrderByUploadDateDesc(inspectionNo);
            
            if (!images.isEmpty()) {
                // Return the status from the first image record (they should all have the same status for an inspection)
                String status = images.get(0).getStatus();
                log.info("Status retrieved from image_inspect table for inspection {}: {}", inspectionNo, status);
                return status != null ? status : "Not Started";
            } else {
                log.info("No images found for inspection {}, returning default status: Not Started", inspectionNo);
                return "Not Started";
            }
            
        } catch (Exception ex) {
            log.error("Error getting status from image_inspect table for inspectionNo: {}", inspectionNo, ex);
            return "Not Started"; // Default to safe status if error occurs
        }
    }

    @Override
    public ApiResponse<List<InspectionRecordsResponse>> getAllInspections() throws BaseException {
        long start = System.currentTimeMillis();
        try {
            List<InspectionRecords> inspectionEntities = inspectionRecordsRepository.findAll();
            List<InspectionRecordsResponse> responseList = new ArrayList<>();

            for (InspectionRecords entity : inspectionEntities) {
                // Get status directly from image_inspect table
                String directStatus = getStatusFromImageInspect(entity.getInspectionNo());
                entity.setStatus(directStatus);
                
                responseList.add(inspectionRecordsMapper.mapEntityToResponse(entity));
            }

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(), responseList);
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(), "Failed to retrieve inspection records");
        }
    }

    @Override
    public ApiResponse<InspectionRecords> updateInspection(InspectionRecords inspectionRecords) throws BaseException {
        long start = System.currentTimeMillis();
        try {
            InspectionRecords existingEntity = inspectionRecordsRepository.findById(inspectionRecords.getId())
                    .orElseThrow(() -> new BaseException(
                            ResponseCodeEnum.BAD_REQUEST.code(),
                            "Inspection record not found with ID: " + inspectionRecords.getId()
                    ));

            // Update fields using mapper
            InspectionRecords updatedEntity = inspectionRecordsMapper.mapRequestToEntityUpdate(existingEntity, inspectionRecords);
            inspectionRecordsRepository.save(updatedEntity);

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(), updatedEntity);

        } catch (BaseException ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw ex;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to update inspection record");
        }
    }

    @Override
    public ApiResponse<Void> deleteInspectionById(Long id) throws BaseException {
        long start = System.currentTimeMillis();
        try {
            InspectionRecords inspectionEntity = inspectionRecordsRepository.findById(id)
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Inspection record not found with ID: " + id));

            inspectionRecordsRepository.delete(inspectionEntity);

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message());
        } catch (BaseException ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw ex;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(), "Failed to delete inspection record");
        }
    }

    @Override
    public ApiResponse<List<InspectionRecords>> filterRecords(FilterRequest filterRequest) throws BaseException {
        ApiResponse<List<InspectionRecords>> response = new ApiResponse<>();
        List<InspectionRecords> inspectionRecords = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try {
            Specification<InspectionRecords> specification = null;

            if (filterRequest.getFilterValues() != null && !filterRequest.getFilterValues().isEmpty()) {
                specification = queryBuilder.buildInspectionSpecification(filterRequest.getFilterValues());
            }

            // Get total count
            long total = (specification != null) ?
                    inspectionRecordsRepository.count(specification) :
                    inspectionRecordsRepository.count();

            // Build pageable
            Integer pageId = getPageId(filterRequest);
            Pageable pageable = PageRequest.of(pageId, filterRequest.getLimit());

            // Execute query with pagination
            Page<InspectionRecords> pageResults = (specification != null) ?
                    inspectionRecordsRepository.findAll(specification, pageable) :
                    inspectionRecordsRepository.findAll(pageable);

            List<InspectionRecords> results = pageResults.getContent();

            if (!results.isEmpty()) {
                inspectionRecords = new ArrayList<>(results);
            }

            response.setResponseCode(ResponseCodeEnum.SUCCESS.code());
            response.setResponseDescription(ResponseCodeEnum.SUCCESS.message());
            response.setResponseData(inspectionRecords);

            PageDetail pageDetail = new PageDetail();
            pageDetail.setTotalRecords(String.valueOf(total));
            pageDetail.setPageNumber(String.valueOf(pageable.getPageNumber() + 1));
            pageDetail.setPageElementCount(String.valueOf(results.size()));
            response.setPageDetail(pageDetail);

            log.info(LoggingAdviceConstants.REQUEST_TERMINATED, System.currentTimeMillis() - startTime, response.getResponseDescription());
            return response;

        } catch (Exception e) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE,
                    System.currentTimeMillis() - startTime,
                    e.getMessage(),
                    StackTraceTracker.displayStackStraceArray(e.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    ResponseCodeEnum.INTERNAL_SERVER_ERROR.message());
        }
    }

    private Integer getPageId(FilterRequest filterRequest) {
        int pageId = 0;
        if (filterRequest.getOffset() != null && filterRequest.getOffset() > 0) {
            pageId = filterRequest.getOffset() / filterRequest.getLimit();
        }
        return pageId;
    }
}