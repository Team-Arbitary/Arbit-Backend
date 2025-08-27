package com.uom.Software_design_competition.domain.service;

import com.uom.Software_design_competition.application.transport.request.FilterRequest;
import com.uom.Software_design_competition.application.transport.request.InspectionRecordsRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.InspectionRecordsResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.domain.entity.InspectionRecords;

import java.util.List;

public interface InspectionManagementService {

    ApiResponse<Void> saveRecord(InspectionRecordsRequest inspectionRecordsRequest) throws BaseException;

    ApiResponse<InspectionRecords> getInspectionById(Long id) throws BaseException;

    ApiResponse<List<InspectionRecordsResponse>> getInspectionsByTransformerNo(String transformerNo) throws BaseException;

    ApiResponse<List<InspectionRecordsResponse>> getAllInspections() throws BaseException;

    ApiResponse<InspectionRecords> updateInspection(InspectionRecords inspectionRecords) throws BaseException;

    ApiResponse<Void> deleteInspectionById(Long id) throws BaseException;

    ApiResponse<List<InspectionRecords>> filterRecords(FilterRequest filterRequest) throws BaseException;

}