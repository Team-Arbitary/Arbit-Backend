// Repository
package com.uom.Software_design_competition.domain.repository;

import com.uom.Software_design_competition.domain.entity.ImageInspect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageInspectRepository extends JpaRepository<ImageInspect, Long> {

    List<ImageInspect> findByTransformerNoOrderByUploadDateDesc(String transformerNo);

    List<ImageInspect> findByInspectionNoOrderByUploadDateDesc(String inspectionNo);

    Optional<ImageInspect> findByTransformerNoAndImageType(String transformerNo, String imageType);

    Optional<ImageInspect> findByInspectionNoAndImageType(String inspectionNo, String imageType);

    @Query("SELECT i FROM ImageInspect i WHERE i.transformerNo = :transformerNo AND i.imageType = 'Baseline'")
    Optional<ImageInspect> findBaselineImageByTransformerNo(@Param("transformerNo") String transformerNo);

    @Query("SELECT i FROM ImageInspect i WHERE i.inspectionNo = :inspectionNo AND i.imageType = 'Thermal'")
    Optional<ImageInspect> findThermalImageByInspectionNo(@Param("inspectionNo") String inspectionNo);

    @Query("SELECT i FROM ImageInspect i WHERE i.inspectionNo = :inspectionNo AND i.imageType = 'Result'")
    Optional<ImageInspect> findResultImageByInspectionNo(@Param("inspectionNo") String inspectionNo);

    boolean existsByTransformerNoAndImageType(String transformerNo, String imageType);

    boolean existsByInspectionNoAndImageType(String inspectionNo, String imageType);
}
