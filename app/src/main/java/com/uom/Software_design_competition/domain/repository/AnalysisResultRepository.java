package com.uom.Software_design_competition.domain.repository;

import com.uom.Software_design_competition.domain.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    /**
     * Find analysis result by inspection number
     */
    Optional<AnalysisResult> findByInspectionNo(String inspectionNo);

    /**
     * Check if analysis result exists for inspection
     */
    boolean existsByInspectionNo(String inspectionNo);

    /**
     * Find analysis results by transformer number
     */
    @Query("SELECT ar FROM AnalysisResult ar WHERE ar.transformerNo = :transformerNo ORDER BY ar.analysisDate DESC")
    java.util.List<AnalysisResult> findByTransformerNo(@Param("transformerNo") String transformerNo);

    /**
     * Find analysis results by status
     */
    @Query("SELECT ar FROM AnalysisResult ar WHERE ar.analysisStatus = :status ORDER BY ar.analysisDate DESC")
    java.util.List<AnalysisResult> findByAnalysisStatus(@Param("status") String status);

    /**
     * Delete analysis result by inspection number
     */
    @Modifying
    @Transactional
    void deleteByInspectionNo(String inspectionNo);
}