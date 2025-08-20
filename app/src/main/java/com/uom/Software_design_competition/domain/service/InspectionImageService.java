package com.uom.Software_design_competition.domain.service;

import com.uom.Software_design_competition.domain.entity.InspectionImage;
import com.uom.Software_design_competition.domain.repository.InspectionImageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class InspectionImageService {
    private final InspectionImageRepository inspectionImageRepository;

    public InspectionImageService(InspectionImageRepository inspectionImageRepository) {
        this.inspectionImageRepository = inspectionImageRepository;
    }

    public InspectionImage saveImage(InspectionImage image) {
        image.setUploadDateTime(LocalDateTime.now());
        return inspectionImageRepository.save(image);
    }

    public Optional<InspectionImage> getImageById(Long id) {
        return inspectionImageRepository.findById(id);
    }

    public void deleteImage(Long id) {
        inspectionImageRepository.deleteById(id);
    }
}