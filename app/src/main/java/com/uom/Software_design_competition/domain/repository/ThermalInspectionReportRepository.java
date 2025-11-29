package com.uom.Software_design_competition.domain.repository;

import com.uom.Software_design_competition.domain.entity.ThermalInspectionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ThermalInspectionReportRepository extends JpaRepository<ThermalInspectionReport, Long> {
    
    // Find current (latest) record for an inspection
    Optional<ThermalInspectionReport> findByInspectionIdAndIsCurrentTrue(Long inspectionId);
    
    // Find all records for an inspection (history) - ordered by version descending
    List<ThermalInspectionReport> findByInspectionIdOrderByVersionDesc(Long inspectionId);
    
    // Find all current records for a transformer
    List<ThermalInspectionReport> findByTransformerNoAndIsCurrentTrueOrderByCreatedAtDesc(String transformerNo);
    
    // Find a specific version for an inspection
    Optional<ThermalInspectionReport> findByInspectionIdAndVersion(Long inspectionId, Integer version);
    
    // Get the max version number for an inspection
    @Query("SELECT COALESCE(MAX(t.version), 0) FROM ThermalInspectionReport t WHERE t.inspection.id = :inspectionId")
    Integer findMaxVersionByInspectionId(@Param("inspectionId") Long inspectionId);
    
    // Mark all records for an inspection as not current
    @Modifying
    @Query("UPDATE ThermalInspectionReport t SET t.isCurrent = false WHERE t.inspection.id = :inspectionId")
    void markAllAsNotCurrentByInspectionId(@Param("inspectionId") Long inspectionId);
}
