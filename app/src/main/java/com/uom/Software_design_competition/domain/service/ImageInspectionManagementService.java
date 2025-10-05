package com.uom.Software_design_competition.domain.service;

import com.uom.Software_design_competition.application.transport.request.ImageRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.ImageInspectResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;

public interface ImageInspectionManagementService {

    // Upload Image (Baseline or Thermal)
    ApiResponse<Void> uploadImage(ImageRequest imageRequest) throws BaseException;

    // Baseline Image Operations
    ApiResponse<ImageInspectResponse> getBaselineImageByTransformerNo(String transformerNo) throws BaseException;
    ApiResponse<Void> updateBaselineImageByTransformerNo(String transformerNo, ImageRequest imageRequest) throws BaseException;
    ApiResponse<Void> deleteBaselineImageByTransformerNo(String transformerNo) throws BaseException;

    // Thermal Image Operations
    ApiResponse<ImageInspectResponse> getThermalImageByInspectionNo(String inspectionNo) throws BaseException;
    ApiResponse<Void> updateThermalImageByInspectionNo(String inspectionNo, ImageRequest imageRequest) throws BaseException;
    ApiResponse<Void> deleteThermalImageByInspectionNo(String inspectionNo) throws BaseException;

    // Result Image Operations
    ApiResponse<ImageInspectResponse> getResultImageByInspectionNo(String inspectionNo) throws BaseException;
}
    // Get comparison images for side-by-side display
   // ApiResponse<ImageInspectResponse[]> getComparisonImages(String transformerNo, String inspectionNo) throws BaseException;

