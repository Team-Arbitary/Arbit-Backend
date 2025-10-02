package com.uom.Software_design_competition.domain.service.impl;

import com.uom.Software_design_competition.application.constant.LoggingAdviceConstants;
import com.uom.Software_design_competition.application.transport.request.ImageRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.ImageInspectResponse;
import com.uom.Software_design_competition.application.util.exception.StackTraceTracker;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.application.util.resultenum.ResponseCodeEnum;
import com.uom.Software_design_competition.domain.entity.ImageInspect;
import com.uom.Software_design_competition.domain.mapper.ImageInspectMapper;
import com.uom.Software_design_competition.domain.repository.AnalysisResultRepository;
import com.uom.Software_design_competition.domain.repository.ImageInspectRepository;
import com.uom.Software_design_competition.domain.service.ImageInspectionManagementService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Slf4j
public class ImageInspectionManagementServiceImpl implements ImageInspectionManagementService {

    private final ImageInspectRepository imageInspectRepository;
    private final ImageInspectMapper imageInspectMapper;
    private final AnalysisResultRepository analysisResultRepository;

    public ImageInspectionManagementServiceImpl(ImageInspectRepository imageInspectRepository,
                                                ImageInspectMapper imageInspectMapper,
                                                AnalysisResultRepository analysisResultRepository) {
        this.imageInspectRepository = imageInspectRepository;
        this.imageInspectMapper = imageInspectMapper;
        this.analysisResultRepository = analysisResultRepository;
    }

    @Override
    public ApiResponse<Void> uploadImage(ImageRequest imageRequest) throws BaseException {
        long startTime = System.currentTimeMillis();

        try {
            // Validation
            if (imageRequest.getImageFile() == null || imageRequest.getImageFile().isEmpty()) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST_INVALID_FIELDS.code(),
                        "Image file is required");
            }

            if (imageRequest.getImageType() == null ||
                    (!imageRequest.getImageType().equals("Baseline") && !imageRequest.getImageType().equals("Thermal"))) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST_INVALID_FIELDS.code(),
                        "Invalid image type. Must be 'Baseline' or 'Thermal'");
            }

            // For baseline images, transformer number is required
            if ("Baseline".equals(imageRequest.getImageType()) &&
                    (imageRequest.getTransformerNo() == null || imageRequest.getTransformerNo().trim().isEmpty())) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST_INVALID_FIELDS.code(),
                        "Transformer number is required for baseline images");
            }

            // For thermal images, inspection number is required
            if ("Thermal".equals(imageRequest.getImageType()) &&
                    (imageRequest.getInspectionNo() == null || imageRequest.getInspectionNo().trim().isEmpty())) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST_INVALID_FIELDS.code(),
                        "Inspection number is required for thermal images");
            }

            // Weather condition validation for baseline images
            if ("Baseline".equals(imageRequest.getImageType()) &&
                    (imageRequest.getWeatherCondition() == null || imageRequest.getWeatherCondition().trim().isEmpty())) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST_INVALID_FIELDS.code(),
                        "Weather condition is required for baseline images");
            }

            // Validate weather condition values
            if (imageRequest.getWeatherCondition() != null &&
                    !imageRequest.getWeatherCondition().equals("Sunny") &&
                    !imageRequest.getWeatherCondition().equals("Cloudy") &&
                    !imageRequest.getWeatherCondition().equals("Rainy")) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST_INVALID_FIELDS.code(),
                        "Invalid weather condition. Must be 'Sunny', 'Cloudy', or 'Rainy'");
            }

            log.info("Uploading {} image", imageRequest.getImageType());

            // Check if image already exists
//            if ("Baseline".equals(imageRequest.getImageType()) &&
//                    imageInspectRepository.existsByTransformerNoAndImageType(imageRequest.getTransformerNo(), "Baseline")) {
//                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
//                        "Baseline image already exists for this transformer");
//            }
//
//            if ("Thermal".equals(imageRequest.getImageType()) &&
//                    imageInspectRepository.existsByInspectionNoAndImageType(imageRequest.getInspectionNo(), "Thermal")) {
//                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
//                        "Thermal image already exists for this inspection");
//            }

            MultipartFile imageFile = imageRequest.getImageFile();
            byte[] imageData = imageFile.getBytes();

            // Validate file size (max 100MB)
            if (imageData.length > 100 * 1024 * 1024) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
                        "Image file size should not exceed 10MB");
            }

            // Validate file type
            String contentType = imageFile.getContentType();
            if (contentType == null || (!contentType.startsWith("image/"))) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
                        "Invalid file type. Only image files are allowed");
            }

            // Set initial status as "Not Started"
            imageRequest.setStatus("Not Started");

            ImageInspect imageInspect = imageInspectMapper.mapRequestToEntityCreate(imageRequest, imageData);
            imageInspectRepository.save(imageInspect);

            // Return response immediately to frontend
            ApiResponse<Void> response = new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(),
                    imageRequest.getImageType() + " image uploaded successfully");

            // Note: Automatic analysis has been disabled. 
            // Use the manual analysis endpoint to trigger analysis when needed.

            return response;

        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - startTime,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to upload image");
        }
    }

    @Override
    public ApiResponse<ImageInspectResponse> getBaselineImageByTransformerNo(String transformerNo) throws BaseException {
        long start = System.currentTimeMillis();
        try {
            Optional<ImageInspect> imageOptional = imageInspectRepository.findBaselineImageByTransformerNo(transformerNo);

            if (imageOptional.isPresent()) {
                ImageInspectResponse response = imageInspectMapper.mapEntityToResponse(imageOptional.get());
                return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(), response);
            } else {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
                        "No baseline image found for transformer: " + transformerNo);
            }

        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to retrieve baseline image for transformer: " + transformerNo);
        }
    }

    @Override
    public ApiResponse<Void> updateBaselineImageByTransformerNo(String transformerNo, ImageRequest imageRequest) throws BaseException {
        long start = System.currentTimeMillis();
        try {
            Optional<ImageInspect> existingImageOptional = imageInspectRepository.findBaselineImageByTransformerNo(transformerNo);

            if (!existingImageOptional.isPresent()) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
                        "No baseline image found for transformer: " + transformerNo);
            }

            ImageInspect existingImage = existingImageOptional.get();

            // Update status to "In progress"
            existingImage.setStatus("In progress");
            imageInspectRepository.save(existingImage);

            byte[] imageData = null;
            if (imageRequest.getImageFile() != null && !imageRequest.getImageFile().isEmpty()) {
                // Validate file size (max 100MB)
                imageData = imageRequest.getImageFile().getBytes();
                if (imageData.length > 100 * 1024 * 1024) {
                    return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Image file size should not exceed 10MB");
                }

                // Validate file type
                String contentType = imageRequest.getImageFile().getContentType();
                if (contentType == null || (!contentType.startsWith("image/"))) {
                    return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Invalid file type. Only image files are allowed");
                }
            }

            ImageInspect updatedImage = imageInspectMapper.mapRequestToEntityUpdate(existingImage, imageRequest, imageData);

            // Update status to "Completed" after successful update
            updatedImage.setStatus("Completed");
            imageInspectRepository.save(updatedImage);

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), "Baseline image updated successfully");

        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to update baseline image for transformer: " + transformerNo);
        }
    }

    @Override
    public ApiResponse<Void> deleteBaselineImageByTransformerNo(String transformerNo) throws BaseException {
        long start = System.currentTimeMillis();
        try {
            Optional<ImageInspect> imageOptional = imageInspectRepository.findBaselineImageByTransformerNo(transformerNo);

            if (!imageOptional.isPresent()) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
                        "No baseline image found for transformer: " + transformerNo);
            }

            // Get the inspection number from baseline image to clean up related data
            String inspectionNo = imageOptional.get().getInspectionNo();
            
            // Delete the baseline image
            imageInspectRepository.delete(imageOptional.get());
            
            // Reset status of remaining thermal image(s) to "Not Started"
            if (inspectionNo != null) {
                Optional<ImageInspect> thermalImage = imageInspectRepository.findThermalImageByInspectionNo(inspectionNo);
                if (thermalImage.isPresent()) {
                    thermalImage.get().setStatus("Not Started");
                    imageInspectRepository.save(thermalImage.get());
                }
                
                // Remove result image if it exists
                Optional<ImageInspect> resultImage = imageInspectRepository.findResultImageByInspectionNo(inspectionNo);
                if (resultImage.isPresent()) {
                    imageInspectRepository.delete(resultImage.get());
                    log.info("Deleted result image for inspection: {}", inspectionNo);
                }
                
                // Remove analysis result if it exists
                if (analysisResultRepository.existsByInspectionNo(inspectionNo)) {
                    analysisResultRepository.deleteByInspectionNo(inspectionNo);
                    log.info("Deleted analysis result for inspection: {}", inspectionNo);
                }
            }
            
            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), "Baseline image deleted successfully");

        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to delete baseline image for transformer: " + transformerNo);
        }
    }

    @Override
    public ApiResponse<ImageInspectResponse> getThermalImageByInspectionNo(String inspectionNo) throws BaseException {
        long start = System.currentTimeMillis();
        try {
            Optional<ImageInspect> imageOptional = imageInspectRepository.findThermalImageByInspectionNo(inspectionNo);

            if (imageOptional.isPresent()) {
                ImageInspectResponse response = imageInspectMapper.mapEntityToResponse(imageOptional.get());
                return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(), response);
            } else {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
                        "No thermal image found for inspection: " + inspectionNo);
            }

        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to retrieve thermal image for inspection: " + inspectionNo);
        }
    }

    @Override
    public ApiResponse<Void> updateThermalImageByInspectionNo(String inspectionNo, ImageRequest imageRequest) throws BaseException {
        long start = System.currentTimeMillis();
        try {
            Optional<ImageInspect> existingImageOptional = imageInspectRepository.findThermalImageByInspectionNo(inspectionNo);

            if (!existingImageOptional.isPresent()) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
                        "No thermal image found for inspection: " + inspectionNo);
            }

            ImageInspect existingImage = existingImageOptional.get();

            // Update status to "In progress"
            existingImage.setStatus("In progress");
            imageInspectRepository.save(existingImage);

            byte[] imageData = null;
            if (imageRequest.getImageFile() != null && !imageRequest.getImageFile().isEmpty()) {
                // Validate file size (max 10MB)
                imageData = imageRequest.getImageFile().getBytes();
                if (imageData.length > 100 * 1024 * 1024) {
                    return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Image file size should not exceed 10MB");
                }

                // Validate file type
                String contentType = imageRequest.getImageFile().getContentType();
                if (contentType == null || (!contentType.startsWith("image/"))) {
                    return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
                            "Invalid file type. Only image files are allowed");
                }
            }

            ImageInspect updatedImage = imageInspectMapper.mapRequestToEntityUpdate(existingImage, imageRequest, imageData);

            // Update status to "Completed" after successful update
            updatedImage.setStatus("Completed");
            imageInspectRepository.save(updatedImage);

            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), "Thermal image updated successfully");

        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to update thermal image for inspection: " + inspectionNo);
        }
    }

    @Override
    public ApiResponse<Void> deleteThermalImageByInspectionNo(String inspectionNo) throws BaseException {
        long start = System.currentTimeMillis();
        try {
            Optional<ImageInspect> imageOptional = imageInspectRepository.findThermalImageByInspectionNo(inspectionNo);

            if (!imageOptional.isPresent()) {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
                        "No thermal image found for inspection: " + inspectionNo);
            }

            // Get transformer number to find related baseline image
            String transformerNo = imageOptional.get().getTransformerNo();
            
            // Delete the thermal image
            imageInspectRepository.delete(imageOptional.get());
            
            // Reset status of baseline image to "Not Started"
            if (transformerNo != null) {
                Optional<ImageInspect> baselineImage = imageInspectRepository.findBaselineImageByTransformerNo(transformerNo);
                if (baselineImage.isPresent()) {
                    baselineImage.get().setStatus("Not Started");
                    imageInspectRepository.save(baselineImage.get());
                }
            }
            
            // Remove result image if it exists
            Optional<ImageInspect> resultImage = imageInspectRepository.findResultImageByInspectionNo(inspectionNo);
            if (resultImage.isPresent()) {
                imageInspectRepository.delete(resultImage.get());
                log.info("Deleted result image for inspection: {}", inspectionNo);
            }
            
            // Remove analysis result if it exists
            if (analysisResultRepository.existsByInspectionNo(inspectionNo)) {
                analysisResultRepository.deleteByInspectionNo(inspectionNo);
                log.info("Deleted analysis result for inspection: {}", inspectionNo);
            }
            
            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), "Thermal image deleted successfully");

        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to delete thermal image for inspection: " + inspectionNo);
        }
    }

    @Override
    public ApiResponse<ImageInspectResponse> getResultImageByInspectionNo(String inspectionNo) throws BaseException {
        long start = System.currentTimeMillis();
        try {
            Optional<ImageInspect> imageOptional = imageInspectRepository.findResultImageByInspectionNo(inspectionNo);

            if (imageOptional.isPresent()) {
                ImageInspectResponse response = imageInspectMapper.mapEntityToResponse(imageOptional.get());
                return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), ResponseCodeEnum.SUCCESS.message(), response);
            } else {
                return new ApiResponse<>(ResponseCodeEnum.BAD_REQUEST.code(),
                        "No result image found for inspection: " + inspectionNo);
            }

        } catch (Exception ex) {
            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
                    "Failed to retrieve result image for inspection: " + inspectionNo);
        }
    }
}
//
//    @Override
//    public ApiResponse<ImageInspectResponse[]> getComparisonImages(String transformerNo, String inspectionNo) throws BaseException {
//        long start = System.currentTimeMillis();
//        try {
//            // Get baseline image by transformer number
//            Optional<ImageInspect> baselineOptional = imageInspectRepository.findBaselineImageByTransformerNo(transformerNo);
//
//            // Get thermal image by inspection number
//            Optional<ImageInspect> thermalOptional = imageInspectRepository.findThermalImageByInspectionNo(inspectionNo);
//
//            ImageInspectResponse[] comparisonImages = new ImageInspectResponse[2];
//
//            if (baselineOptional.isPresent()) {
//                comparisonImages[0] = imageInspectMapper.mapEntityToResponse(baselineOptional.get());
//            }
//
//            if (thermalOptional.isPresent()) {
//                comparisonImages[1] = imageInspectMapper.mapEntityToResponse(thermalOptional.get());
//            }
//
//            return new ApiResponse<>(ResponseCodeEnum.SUCCESS.code(), "Comparison images retrieved successfully", comparisonImages);
//
//        } catch (Exception ex) {
//            log.error(LoggingAdviceConstants.EXCEPTION_STACK_TRACE, System.currentTimeMillis() - start,
//                    ex.getMessage(), StackTraceTracker.displayStackStraceArray(ex.getStackTrace()));
//            throw new BaseException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
//                    "Failed to retrieve comparison images");
//        }
//    }
//}
