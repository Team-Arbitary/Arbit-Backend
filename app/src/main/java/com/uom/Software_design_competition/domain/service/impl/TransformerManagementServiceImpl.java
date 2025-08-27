package com.uom.Software_design_competition.domain.service.impl;

import com.uom.Software_design_competition.application.constant.LoggingAdviceConstants;
import com.uom.Software_design_competition.application.transport.request.FilterRequest;
import com.uom.Software_design_competition.application.transport.request.TransformerRecordsRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;

import com.uom.Software_design_competition.application.transport.response.PageDetail;
import com.uom.Software_design_competition.application.transport.response.TransformerRecordsResponse;
import com.uom.Software_design_competition.application.util.exception.StackTraceTracker;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.application.util.resultenum.ResponseCodeEnum;
import com.uom.Software_design_competition.domain.entity.TransformerRecords;
import com.uom.Software_design_competition.domain.mapper.TransformerRecordsMapper;
import com.uom.Software_design_competition.domain.repository.TransformerRecordsRepository;
import com.uom.Software_design_competition.domain.service.TransformerManagementService;

import com.uom.Software_design_competition.domain.util.QueryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TransformerManagementServiceImpl implements TransformerManagementService {

    private final TransformerRecordsRepository transformerRecordsRepository;
    private final TransformerRecordsMapper transformerRecordsMapper;
    private final QueryBuilder queryBuilder;

    public TransformerManagementServiceImpl(TransformerRecordsRepository transformerRecordsRepository,TransformerRecordsMapper transformerRecordsMapper, QueryBuilder queryBuilder) {
        this.transformerRecordsRepository = transformerRecordsRepository;
        this.transformerRecordsMapper = transformerRecordsMapper;
        this.queryBuilder = queryBuilder;
    }

       @Override
    public ApiResponse<Void> saveRecord(TransformerRecordsRequest transformerRecordsRequest) throws BaseException {
        long startTime = System.currentTimeMillis();

        if (transformerRecordsRequest.getRegions() == null ||
                transformerRecordsRequest.getPoleNo() == null ||
                transformerRecordsRequest.getTransformerNo() == null ||
                transformerRecordsRequest.getType() == null ) {
            return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST_INVALID_FIELDS.code(),
                    ResponseCodeEnum.BAD_REQUEST_INVALID_FIELDS.message());
        }
        try {
            log.info("Creating transformer record: {}", transformerRecordsRequest);

            // Check if transformer number already exists
//            if (transformerRecordsRepository.existsByTransformerNo(transformerRecordsRequest.getTransformerNo())) {
//                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
//                        "Transformer number already exists");
//            }

            TransformerRecords transformerrecords = transformerRecordsMapper.mapRequestToEntityCreate(transformerRecordsRequest);
            transformerRecordsRepository.save(transformerrecords);
            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message());
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - startTime, ex.getMessage(),StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.LOG_NOT_CONNECTED.code(), ResponseCodeEnum.LOG_NOT_CONNECTED.message());

        }
    }


    @Override
    public ApiResponse<TransformerRecords> getTransformerById(Long id) throws BaseException {
        long start = System.currentTimeMillis();
        try {
            TransformerRecords transformerRecord= transformerRecordsRepository.findById(id)
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Transformer not found with ID: " + id));

          //  TransformerRecordsResponse response = transformerRecordsMapper.mapEntityToResponse(transformerEntity);
            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(), transformerRecord);
        } catch (BaseException ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw ex;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(), "Failed to retrieve transformer record");
        }
    }

    @Override
    public ApiResponse<List<TransformerRecordsResponse>> getAllTransformers() throws BaseException {
        long start = System.currentTimeMillis();
        try {
            List<TransformerRecords> transformerEntities = transformerRecordsRepository.findAll();
            List<TransformerRecordsResponse> responseList = new ArrayList<>();

            for (TransformerRecords entity : transformerEntities) {
                responseList.add(transformerRecordsMapper.mapEntityToResponse(entity));
            }

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(), responseList);
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(), "Failed to retrieve transformer records");
        }
    }

    @Override
    public ApiResponse<TransformerRecords> updateTransformer(TransformerRecords transformerRecords) throws BaseException {
        long start = System.currentTimeMillis();
        try {
            TransformerRecords existingEntity = transformerRecordsRepository.findById(transformerRecords.getId())
                    .orElseThrow(() -> new BaseException(
                            ResponseCodeEnum.BAD_REQUEST.code(),
                            "Transformer not found with ID: " + transformerRecords.getId()
                    ));

            // update fields using mapper
            TransformerRecords updatedEntity = transformerRecordsMapper.mapRequestToEntityUpdate(existingEntity, transformerRecords);
            transformerRecordsRepository.save(updatedEntity);

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(), updatedEntity);

        } catch (BaseException ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw ex;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to update transformer record");
        }
    }

    @Override
    public ApiResponse<Void> deleteTransformerById(Long id) throws BaseException {
        long start = System.currentTimeMillis();
        try {
            TransformerRecords transformerEntity = transformerRecordsRepository.findById(id)
                    .orElseThrow(() -> new BaseException(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Transformer not found with ID: " + id));

            transformerRecordsRepository.delete(transformerEntity);

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message());
        } catch (BaseException ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw ex;
        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(), "Failed to delete transformer record");
        }
    }

//    private void validateTransformerRequest(TransformerRecordsRequest request) throws BaseException {
//        if (request.getRegions() == null || request.getRegions().trim().isEmpty() ||
//                request.getPoleNo() == null || request.getPoleNo().trim().isEmpty() ||
//                request.getTransformerNo() == null || request.getTransformerNo().trim().isEmpty() ||
//                request.getType() == null || request.getType().trim().isEmpty() ||
//                request.getLocation() == null || request.getLocation().trim().isEmpty())
//                {
//
//            throw new BaseException(ResponseCodeEnum.BAD_REQUEST.code(), "Mandatory fields are missing");
//        }
//    }

    @Override
    public ApiResponse<List<TransformerRecords>> filterRecords(FilterRequest filterRequest) throws BaseException {
        ApiResponse<List<TransformerRecords>> response = new ApiResponse<>();
        List<TransformerRecords> transformerRecords = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try {
            Specification<TransformerRecords> specification = null;

            if (filterRequest.getFilterValues() != null && !filterRequest.getFilterValues().isEmpty()) {
                specification = queryBuilder.buildTransformerSpecification(filterRequest.getFilterValues());
            }

            // Get total count
            long total = transformerRecordsRepository.count(specification);

            // Build pageable
            Integer pageId = getPageId(filterRequest);
            Pageable pageable = PageRequest.of(pageId, filterRequest.getLimit());

            // Execute query with pagination
            Page<TransformerRecords> pageResults = transformerRecordsRepository.findAll(specification, pageable);
            List<TransformerRecords> results = pageResults.getContent();

            if (!results.isEmpty()) {
                transformerRecords = new ArrayList<>(results);
            }

            response.setResponseCode(ResponseCodeEnum.SUCCESS.code());
            response.setResponseDescription(ResponseCodeEnum.SUCCESS.message());
            response.setResponseData(transformerRecords);

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