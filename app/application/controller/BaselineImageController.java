package com.uom.Software_design_competition.application.controller;

import com.uom.Software_design_competition.application.transport.request.BaselineImageRequest;
import com.uom.Software_design_competition.application.transport.response.BaselineImageResponse;
import com.uom.Software_design_competition.domain.entity.BaselineImage;
import com.uom.Software_design_competition.domain.service.BaselineImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/baseline-images")
public class BaselineImageController {

    private final BaselineImageService baselineImageService;

    public BaselineImageController(BaselineImageService baselineImageService) {
        this.baselineImageService = baselineImageService;
    }

    @PostMapping
    public ResponseEntity<BaselineImageResponse> uploadImage(@RequestBody BaselineImageRequest request) {
        BaselineImage image = new BaselineImage(
                null,
                request.getTransformerId(),
                request.getName(),
                request.getType(),
                request.getData(),
                null,
                request.getUploader(),
                request.getEnvironmentCondition()
        );
        BaselineImage savedImage = baselineImageService.saveImage(image);
        return ResponseEntity.ok(new BaselineImageResponse(
                savedImage.getId(),
                savedImage.getTransformerId(),
                savedImage.getName(),
                savedImage.getType(),
                savedImage.getUploader(),
                savedImage.getEnvironmentCondition()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaselineImage> getImage(@PathVariable String id) {
        Optional<BaselineImage> image = baselineImageService.getImageById(id);
        return image.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable String id) {
        baselineImageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}