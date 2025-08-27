package com.uom.Software_design_competition.domain.service;

import com.uom.Software_design_competition.domain.entity.BaselineImage;
import com.uom.Software_design_competition.domain.repository.BaselineImageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BaselineImageService {
    private final BaselineImageRepository baselineImageRepository;

    public BaselineImageService(BaselineImageRepository baselineImageRepository) {
        this.baselineImageRepository = baselineImageRepository;
    }

    public BaselineImage saveImage(BaselineImage image) {
        image.setUploadDateTime(LocalDateTime.now());
        return baselineImageRepository.save(image);
    }

    public Optional<BaselineImage> getImageById(Long id) {
        return baselineImageRepository.findById(id);
    }

    public void deleteImage(Long id) {
        baselineImageRepository.deleteById(id);
    }
}