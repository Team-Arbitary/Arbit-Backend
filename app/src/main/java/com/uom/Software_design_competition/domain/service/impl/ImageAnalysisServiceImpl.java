package com.uom.Software_design_competition.domain.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.application.util.resultenum.ResponseCodeEnum;
import com.uom.Software_design_competition.domain.entity.AnalysisResult;
import com.uom.Software_design_competition.domain.entity.ImageInspect;
import com.uom.Software_design_competition.domain.repository.AnalysisResultRepository;
import com.uom.Software_design_competition.domain.repository.ImageInspectRepository;
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
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${analysis.api.url:http://localhost:8000}")
    private String analysisApiUrl;

    public ImageAnalysisServiceImpl(ImageInspectRepository imageInspectRepository,
                                   AnalysisResultRepository analysisResultRepository,
                                   RestTemplate restTemplate,
                                   ObjectMapper objectMapper) {
        this.imageInspectRepository = imageInspectRepository;
        this.analysisResultRepository = analysisResultRepository;
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
            ResponseEntity<String> response = restTemplate.exchange(
                    analysisApiUrl + "/detect",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                // For JSON response, we need to get the annotated image separately
                String jsonResult = response.getBody();
                
                // Get annotated image
                body.replace("return_format", List.of("annotated"));
                HttpEntity<MultiValueMap<String, Object>> annotatedRequest = new HttpEntity<>(body, headers);
                
                ResponseEntity<byte[]> annotatedResponse = restTemplate.exchange(
                        analysisApiUrl + "/detect",
                        HttpMethod.POST,
                        annotatedRequest,
                        byte[].class
                );

                byte[] annotatedImageData = annotatedResponse.getBody();
                
                // Save analysis result in AnalysisResult table
                AnalysisResult analysisResult = new AnalysisResult(
                        inspectionNo, 
                        transformerNo, 
                        annotatedImageData, 
                        jsonResult, 
                        "SUCCESS"
                );
                analysisResult.setProcessingTimeMs(System.currentTimeMillis() - startTime);
                analysisResultRepository.save(analysisResult);

                // Save result image in ImageInspect table with "Result" type
                ImageInspect resultImage = new ImageInspect();
                resultImage.setInspectionNo(inspectionNo);
                resultImage.setTransformerNo(transformerNo);
                resultImage.setImageType("Result");
                resultImage.setImageData(annotatedImageData);
                resultImage.setStatus("Completed");
                resultImage.setUploadDate(new java.text.SimpleDateFormat("EEE, MMM dd, yyyy hh:mm a").format(new java.util.Date()));
                resultImage.setWeatherCondition("Analysis Result");
                imageInspectRepository.save(resultImage);

                // Update baseline and thermal image statuses to "Completed"
                updateImageStatuses(baselineImage.get(), thermalImage.get(), "Completed");

                log.info("Analysis completed successfully for inspection {}", inspectionNo);
                return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), 
                        "Analysis completed successfully", analysisResult);

            } else {
                throw new RuntimeException("Analysis API returned status: " + response.getStatusCode());
            }

        } catch (Exception ex) {
            log.error("Error performing analysis for inspection {}: {}", inspectionNo, ex.getMessage());
            
            // Save failed analysis result
            AnalysisResult failedResult = new AnalysisResult();
            failedResult.setInspectionNo(inspectionNo);
            failedResult.setTransformerNo(transformerNo);
            failedResult.setAnalysisStatus("FAILED");
            failedResult.setErrorMessage(ex.getMessage());
            failedResult.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            
            analysisResultRepository.save(failedResult);

            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(), 
                    "Analysis failed: " + ex.getMessage());
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
}