package com.uom.Software_design_competition.domain.service;

import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.domain.entity.AnalysisResult;

public interface ImageAnalysisService {

    /**
     * Check if both baseline and thermal images exist for an inspection
     * and trigger analysis if ready (async)
     */
    void checkAndTriggerAnalysis(String inspectionNo, String transformerNo);

    /**
     * Perform analysis using external API
     */
    ApiResponse<AnalysisResult> performAnalysis(String inspectionNo, String transformerNo) throws BaseException;

    /**
     * Perform analysis using only inspection number (gets transformer from thermal image)
     */
    ApiResponse<AnalysisResult> performAnalysisWithInspectionNo(String inspectionNo) throws BaseException;

    /**
     * Get analysis result by inspection number
     */
    ApiResponse<AnalysisResult> getAnalysisResult(String inspectionNo) throws BaseException;

    /**
     * Update inspection status based on image availability and analysis state (async)
     */
    void updateInspectionStatus(String inspectionNo, String transformerNo);

    /**
     * Get all analysis results for a transformer
     */
    ApiResponse<java.util.List<AnalysisResult>> getAnalysisResultsByTransformer(String transformerNo) throws BaseException;

    /**
     * Test method to directly update inspection record status (for testing ID fallback logic)
     */
    String testStatusUpdate(String inspectionNo) throws BaseException;
}