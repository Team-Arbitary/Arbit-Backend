package com.uom.Software_design_competition.domain.service;

import com.uom.Software_design_competition.application.transport.request.FilterRequest;
import com.uom.Software_design_competition.application.transport.request.TransformerRecordsRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.TransformerRecordsResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.domain.entity.TransformerRecords;

import java.util.List;

public interface TransformerManagementService {

    ApiResponse<Void> saveRecord(TransformerRecordsRequest transformerRecordsRequest) throws BaseException;

    ApiResponse<TransformerRecords> getTransformerById(Long id) throws BaseException;

    ApiResponse<List<TransformerRecordsResponse>> getAllTransformers() throws BaseException;

    ApiResponse<TransformerRecords> updateTransformer(TransformerRecords transformerRecords) throws BaseException ;

    ApiResponse<Void> deleteTransformerById(Long id) throws BaseException;

   ApiResponse<List<TransformerRecords>> filterRecords(FilterRequest filterRequest) throws BaseException ;
}