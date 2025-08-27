package com.uom.Software_design_competition.domain.mapper;

import com.uom.Software_design_competition.application.transport.request.ImageRequest;
import com.uom.Software_design_competition.application.transport.response.ImageInspectResponse;
import com.uom.Software_design_competition.domain.entity.ImageInspect;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Component
public class ImageInspectMapper {

    public ImageInspect mapRequestToEntityCreate(ImageRequest request, byte[] imageData) {
        String uploadDate = new SimpleDateFormat("EEE, MMM dd, yyyy hh:mm a").format(new Date());

        return ImageInspect.builder()
                .transformerNo(request.getTransformerNo())
                .inspectionNo(request.getInspectionNo())
                .imageType(request.getImageType())
                .weatherCondition(request.getWeatherCondition())
                .uploadDate(uploadDate)
                .imageData(imageData)
                .status(request.getStatus() != null ? request.getStatus() : "In progress")
                .build();
    }

    public ImageInspect mapRequestToEntityUpdate(ImageInspect existingEntity, ImageRequest request, byte[] imageData) {
        if (request.getTransformerNo() != null) {
            existingEntity.setTransformerNo(request.getTransformerNo());
        }
        if (request.getInspectionNo() != null) {
            existingEntity.setInspectionNo(request.getInspectionNo());
        }
        if (request.getImageType() != null) {
            existingEntity.setImageType(request.getImageType());
        }
        if (request.getWeatherCondition() != null) {
            existingEntity.setWeatherCondition(request.getWeatherCondition());
        }
        if (request.getStatus() != null) {
            existingEntity.setStatus(request.getStatus());
        }
        if (imageData != null) {
            existingEntity.setImageData(imageData);
            existingEntity.setUploadDate(new SimpleDateFormat("EEE, MMM dd, yyyy hh:mm a").format(new Date()));
        }

        return existingEntity;
    }

    public ImageInspectResponse mapEntityToResponse(ImageInspect entity) {
        ImageInspectResponse response = new ImageInspectResponse();
        response.setId(entity.getId());
        response.setTransformerNo(entity.getTransformerNo());
        response.setInspectionNo(entity.getInspectionNo());
        response.setImageType(entity.getImageType());
        response.setWeatherCondition(entity.getWeatherCondition());
        response.setUploadDate(entity.getUploadDate());
        response.setStatus(entity.getStatus());
       // response.setCreatedAt(entity.getCreatedAt());

        // Convert byte array to Base64 for frontend display
        if (entity.getImageData() != null) {
            response.setImageBase64("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(entity.getImageData()));
        }

        return response;
    }

    public ImageInspectResponse mapEntityToResponseWithoutImage(ImageInspect entity) {
        ImageInspectResponse response = new ImageInspectResponse();
        response.setId(entity.getId());
        response.setTransformerNo(entity.getTransformerNo());
        response.setInspectionNo(entity.getInspectionNo());
        response.setImageType(entity.getImageType());
        response.setWeatherCondition(entity.getWeatherCondition());
        response.setUploadDate(entity.getUploadDate());
        response.setStatus(entity.getStatus());
      //  response.setCreatedAt(entity.getCreatedAt());

        return response;
    }
}