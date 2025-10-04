package com.uom.Software_design_competition.domain.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.application.util.resultenum.ResponseCodeEnum;
import com.uom.Software_design_competition.domain.entity.AnalysisResult;
import com.uom.Software_design_competition.domain.entity.ImageInspect;
import com.uom.Software_design_competition.domain.entity.InspectionRecords;
import com.uom.Software_design_competition.domain.repository.AnalysisResultRepository;
import com.uom.Software_design_competition.domain.repository.ImageInspectRepository;
import com.uom.Software_design_competition.domain.repository.InspectionRecordsRepository;
import com.uom.Software_design_competition.domain.service.ImageAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class ImageAnalysisServiceImpl implements ImageAnalysisService {

    private final ImageInspectRepository imageInspectRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final InspectionRecordsRepository inspectionRecordsRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${analysis.api.url:http://192.248.10.121:8000}")
    private String analysisApiUrl;

    public ImageAnalysisServiceImpl(ImageInspectRepository imageInspectRepository,
                                   AnalysisResultRepository analysisResultRepository,
                                   InspectionRecordsRepository inspectionRecordsRepository,
                                   RestTemplate restTemplate,
                                   ObjectMapper objectMapper) {
        this.imageInspectRepository = imageInspectRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.inspectionRecordsRepository = inspectionRecordsRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    @Async
    public void checkAndTriggerAnalysis(String inspectionNo, String transformerNo) {
        try {
            // Check if both images exist
            Optional<ImageInspect> baselineImage = imageInspectRepository.findBaselineImageByTransformerNo(transformerNo);
            Optional<ImageInspect> thermalImage = imageInspectRepository.findThermalImageByInspectionNo(inspectionNo);

            if (baselineImage.isPresent() && thermalImage.isPresent()) {
                // Check if analysis already exists
                if (!analysisResultRepository.existsByInspectionNo(inspectionNo)) {
                    log.info("Both images found for inspection {}. Triggering analysis...", inspectionNo);
                    
                    // Update status to "In Progress"
                    updateImageStatuses(baselineImage.get(), thermalImage.get(), "In Progress");
                    
                    // Trigger analysis
                    performAnalysis(inspectionNo, transformerNo);
                } else {
                    log.info("Analysis already exists for inspection {}", inspectionNo);
                }
            } else {
                log.info("Missing images for inspection {}. Baseline: {}, Thermal: {}", 
                        inspectionNo, baselineImage.isPresent(), thermalImage.isPresent());
                
                // Update status to "Not Started" if images are missing
                if (baselineImage.isPresent()) {
                    updateImageStatus(baselineImage.get(), "Not Started");
                }
                if (thermalImage.isPresent()) {
                    updateImageStatus(thermalImage.get(), "Not Started");
                }
            }
        } catch (Exception ex) {
            log.error("Error checking and triggering analysis for inspection {}: {}", inspectionNo, ex.getMessage());
            // Log error but don't throw exception for async method
        }
    }

    @Override
    public ApiResponse<AnalysisResult> performAnalysis(String inspectionNo, String transformerNo) throws BaseException {
        long startTime = System.currentTimeMillis();
        
        try {
            // Get baseline and thermal images
            Optional<ImageInspect> baselineImage = imageInspectRepository.findBaselineImageByTransformerNo(transformerNo);
            Optional<ImageInspect> thermalImage = imageInspectRepository.findThermalImageByInspectionNo(inspectionNo);

            if (!baselineImage.isPresent() || !thermalImage.isPresent()) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(), 
                        "Both baseline and thermal images are required for analysis");
            }

            // Prepare multipart request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            
            // Add baseline image
            ByteArrayResource baselineResource = new ByteArrayResource(baselineImage.get().getImageData()) {
                @Override
                public String getFilename() {
                    return "baseline.jpg";
                }
            };
            body.add("baseline", baselineResource);

            // Add thermal (maintenance) image
            ByteArrayResource thermalResource = new ByteArrayResource(thermalImage.get().getImageData()) {
                @Override
                public String getFilename() {
                    return "maintenance.jpg";
                }
            };
            body.add("maintenance", thermalResource);

            // Add transformer ID and return format
            body.add("transformer_id", inspectionNo);
            body.add("return_format", "json");

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Call analysis API
            log.info("Calling analysis API for inspection {}", inspectionNo);
            ResponseEntity<String> response;
            try {
                response = restTemplate.exchange(
                        analysisApiUrl + "/detect",
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );
                log.info("Analysis API response status: {}", response.getStatusCode());
            } catch (Exception apiEx) {
                log.error("Error calling analysis API: {}", apiEx.getMessage());
                throw new RuntimeException("Analysis API is not available: " + apiEx.getMessage());
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                // For JSON response, we need to get the annotated image separately
                String jsonResult = response.getBody();
                
                // Get annotated image
                body.replace("return_format", List.of("annotated"));
                HttpEntity<MultiValueMap<String, Object>> annotatedRequest = new HttpEntity<>(body, headers);
                
                ResponseEntity<byte[]> annotatedResponse;
                try {
                    annotatedResponse = restTemplate.exchange(
                            analysisApiUrl + "/detect",
                            HttpMethod.POST,
                            annotatedRequest,
                            byte[].class
                    );
                    log.info("Annotated image API response status: {}", annotatedResponse.getStatusCode());
                } catch (Exception apiEx) {
                    log.error("Error getting annotated image from API: {}", apiEx.getMessage());
                    throw new RuntimeException("Failed to get annotated image: " + apiEx.getMessage());
                }

                byte[] annotatedImageData = annotatedResponse.getBody();
                
                // Debug logging
                if (annotatedImageData == null) {
                    log.warn("Received null annotated image data from analysis API");
                    annotatedImageData = new byte[0]; // Use empty array instead of null
                } else {
                    log.info("Received annotated image data: {} bytes, type: {}", 
                            annotatedImageData.length, annotatedImageData.getClass().getName());
                }
                
                // Save analysis result in AnalysisResult table with proper error handling
                AnalysisResult analysisResult;
                try {
                    analysisResult = new AnalysisResult(
                            inspectionNo, 
                            transformerNo, 
                            annotatedImageData, 
                            jsonResult, 
                            "SUCCESS"
                    );
                    analysisResult.setProcessingTimeMs(System.currentTimeMillis() - startTime);
                    
                    log.info("Saving analysis result for inspection {}, image data size: {} bytes", 
                            inspectionNo, annotatedImageData != null ? annotatedImageData.length : 0);
                    
                    analysisResultRepository.save(analysisResult);
                    log.info("Analysis result saved successfully for inspection {}", inspectionNo);
                } catch (Exception saveEx) {
                    log.error("Error saving analysis result for inspection {}: {}", inspectionNo, saveEx.getMessage(), saveEx);
                    throw new RuntimeException("Failed to save analysis result: " + saveEx.getMessage(), saveEx);
                }

                // Save result image in ImageInspect table with "Result" type
                ImageInspect resultImage = new ImageInspect();
                resultImage.setInspectionNo(inspectionNo);
                resultImage.setTransformerNo(transformerNo);
                resultImage.setImageType("Result");
                resultImage.setImageData(annotatedImageData);
                resultImage.setStatus("Complete");
                resultImage.setUploadDate(new java.text.SimpleDateFormat("EEE, MMM dd, yyyy hh:mm a").format(new java.util.Date()));
                resultImage.setWeatherCondition("Analysis Result");
                imageInspectRepository.save(resultImage);

                // Update baseline and thermal image statuses to "Complete"
                updateImageStatuses(baselineImage.get(), thermalImage.get(), "Complete");

                // Update inspection record status to "Completed"
                updateInspectionRecordStatus(inspectionNo, "Completed");

                log.info("Analysis completed successfully for inspection {}", inspectionNo);
                return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), 
                        "Analysis completed successfully", analysisResult);

            } else {
                throw new RuntimeException("Analysis API returned status: " + response.getStatusCode());
            }

        } catch (Exception ex) {
            log.error("Error performing analysis for inspection {}: {}", inspectionNo, ex.getMessage(), ex);
            
            // Save failed analysis result with proper error handling
            try {
                AnalysisResult failedResult = new AnalysisResult();
                failedResult.setInspectionNo(inspectionNo);
                failedResult.setTransformerNo(transformerNo);
                failedResult.setAnalysisStatus("FAILED");
                failedResult.setErrorMessage(ex.getMessage());
                failedResult.setProcessingTimeMs(System.currentTimeMillis() - startTime);
                failedResult.setAnalysisDate(java.time.LocalDateTime.now());
                // Explicitly set annotated image data to null for failed cases
                failedResult.setAnnotatedImageData(null);
                
                analysisResultRepository.save(failedResult);
                log.info("Failed analysis result saved for inspection {}", inspectionNo);
            } catch (Exception saveEx) {
                log.error("Error saving failed analysis result for inspection {}: {}", inspectionNo, saveEx.getMessage(), saveEx);
            }

            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(), 
                    "Analysis failed: " + ex.getMessage());
        }
    }

    @Override
    public ApiResponse<AnalysisResult> performAnalysisWithInspectionNo(String inspectionNo) throws BaseException {
        try {
            // Get the thermal image first to find the transformer number
            Optional<ImageInspect> thermalImage = imageInspectRepository.findThermalImageByInspectionNo(inspectionNo);
            
            if (!thermalImage.isPresent()) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(), 
                        "Thermal image not found for inspection: " + inspectionNo);
            }
            
            String transformerNo = thermalImage.get().getTransformerNo();
            if (transformerNo == null || transformerNo.trim().isEmpty()) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(), 
                        "Transformer number not found in thermal image data");
            }
            
            // Now perform analysis with both inspection and transformer numbers
            return performAnalysis(inspectionNo, transformerNo);
            
        } catch (Exception ex) {
            log.error("Error performing analysis for inspection {}: {}", inspectionNo, ex.getMessage());
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(), 
                    "Failed to perform analysis: " + ex.getMessage());
        }
    }

    @Override
    public ApiResponse<AnalysisResult> getAnalysisResult(String inspectionNo) throws BaseException {
        try {
            Optional<AnalysisResult> result = analysisResultRepository.findByInspectionNo(inspectionNo);
            
            if (result.isPresent()) {
                return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), 
                        "Analysis result retrieved successfully", result.get());
            } else {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(), 
                        "No analysis result found for inspection: " + inspectionNo);
            }
        } catch (Exception ex) {
            log.error("Error retrieving analysis result for inspection {}: {}", inspectionNo, ex.getMessage());
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(), 
                    "Failed to retrieve analysis result: " + ex.getMessage());
        }
    }

    @Override
    @Async
    public void updateInspectionStatus(String inspectionNo, String transformerNo) {
        checkAndTriggerAnalysis(inspectionNo, transformerNo);
    }

    @Override
    public ApiResponse<List<AnalysisResult>> getAnalysisResultsByTransformer(String transformerNo) throws BaseException {
        try {
            List<AnalysisResult> results = analysisResultRepository.findByTransformerNo(transformerNo);
            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), 
                    "Analysis results retrieved successfully", results);
        } catch (Exception ex) {
            log.error("Error retrieving analysis results for transformer {}: {}", transformerNo, ex.getMessage());
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(), 
                    "Failed to retrieve analysis results: " + ex.getMessage());
        }
    }

    private void updateImageStatuses(ImageInspect baselineImage, ImageInspect thermalImage, String status) {
        updateImageStatus(baselineImage, status);
        updateImageStatus(thermalImage, status);
    }

    private void updateImageStatus(ImageInspect image, String status) {
        image.setStatus(status);
        imageInspectRepository.save(image);
    }

    private void updateInspectionRecordStatus(String inspectionNo, String status) {
        try {
            // First try to find by exact inspection number match
            Optional<InspectionRecords> inspectionRecord = inspectionRecordsRepository.findByInspectionNo(inspectionNo);
            
            // If not found and inspectionNo is a number, try to find by ID
            if (!inspectionRecord.isPresent()) {
                try {
                    Long id = Long.parseLong(inspectionNo);
                    inspectionRecord = inspectionRecordsRepository.findById(id);
                    log.info("Found inspection record by ID: {} for inspection number: {}", id, inspectionNo);
                } catch (NumberFormatException e) {
                    log.debug("Inspection number {} is not a valid ID", inspectionNo);
                }
            }
            
            if (inspectionRecord.isPresent()) {
                InspectionRecords record = inspectionRecord.get();
                String oldStatus = record.getStatus();
                record.setStatus(status);
                inspectionRecordsRepository.save(record);
                log.info("Updated inspection record status for inspection {} (record ID: {}) from '{}' to '{}'", 
                        inspectionNo, record.getId(), oldStatus, status);
            } else {
                log.warn("Inspection record not found for inspection number: {}", inspectionNo);
            }
        } catch (Exception ex) {
            log.error("Error updating inspection record status for inspection {}: {}", inspectionNo, ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional
    public String testStatusUpdate(String inspectionNo) throws BaseException {
        try {
            log.info("Testing status update for inspection number: {}", inspectionNo);
            
            // First try to find by exact inspection number match
            Optional<InspectionRecords> inspectionRecord = inspectionRecordsRepository.findByInspectionNo(inspectionNo);
            String foundBy = "inspection_no";
            
            // If not found and inspectionNo is a number, try to find by ID
            if (!inspectionRecord.isPresent()) {
                try {
                    Long id = Long.parseLong(inspectionNo);
                    inspectionRecord = inspectionRecordsRepository.findById(id);
                    foundBy = "id=" + id;
                    log.info("Found inspection record by ID: {} for inspection number: {}", id, inspectionNo);
                } catch (NumberFormatException e) {
                    log.debug("Inspection number {} is not a valid ID", inspectionNo);
                    return "FAILED: Inspection number '" + inspectionNo + "' not found by inspection_no or ID";
                }
            }
            
            if (inspectionRecord.isPresent()) {
                InspectionRecords record = inspectionRecord.get();
                String oldStatus = record.getStatus();
                record.setStatus("Completed");
                inspectionRecordsRepository.save(record);
                log.info("Test: Updated inspection record status for inspection {} (found by: {}, record ID: {}) from '{}' to 'Completed'", 
                        inspectionNo, foundBy, record.getId(), oldStatus);
                return String.format("SUCCESS: Updated record ID=%d (inspection_no='%s', found by: %s) from '%s' to 'Completed'",
                        record.getId(), record.getInspectionNo(), foundBy, oldStatus);
            } else {
                log.warn("Inspection record not found for inspection number: {}", inspectionNo);
                return "FAILED: Inspection record not found for inspection number: " + inspectionNo;
            }
        } catch (Exception ex) {
            log.error("Error testing status update for inspection {}: {}", inspectionNo, ex.getMessage(), ex);
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(), 
                    "Test failed: " + ex.getMessage());
        }
    }
}