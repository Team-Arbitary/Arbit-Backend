package com.uom.Software_design_competition.application.controller;

import com.uom.Software_design_competition.application.transport.request.InspectionImageRequest;
import com.uom.Software_design_competition.application.transport.response.InspectionImageResponse;
import com.uom.Software_design_competition.domain.entity.InspectionImage;
import com.uom.Software_design_competition.domain.service.InspectionImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/inspection-images")
public class InspectionImageController {

    private final InspectionImageService inspectionImageService;

    public InspectionImageController(InspectionImageService inspectionImageService) {
        this.inspectionImageService = inspectionImageService;
    }

    @PostMapping
    public ResponseEntity<InspectionImageResponse> uploadImage(@RequestBody InspectionImageRequest request) {
        InspectionImage image = new InspectionImage(
                null,
                request.getInspectionId(),
                request.getName(),
                request.getType(),
                request.getData(),
                null,
                request.getUploader(),
                request.getEnvironmentCondition()
        );
        InspectionImage savedImage = inspectionImageService.saveImage(image);
        return ResponseEntity.ok(new InspectionImageResponse(
                savedImage.getId(),
                savedImage.getInspectionId(),
                savedImage.getName(),
                savedImage.getType(),
                savedImage.getUploader(),
                savedImage.getEnvironmentCondition()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspectionImage> getImage(@PathVariable String id) {
        Optional<InspectionImage> image = inspectionImageService.getImageById(id);
        return image.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable String id) {
        inspectionImageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}