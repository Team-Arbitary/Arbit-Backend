package com.uom.Software_design_competition.domain.repository;

import com.uom.Software_design_competition.domain.entity.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {
    
    // Find current (latest) record for an inspection
    Optional<MaintenanceRecord> findByInspectionIdAndIsCurrentTrue(Long inspectionId);
    
    // Find all records for an inspection (history) - ordered by version descending
    List<MaintenanceRecord> findByInspectionIdOrderByVersionDesc(Long inspectionId);
    
    // Find all current records for a transformer
    List<MaintenanceRecord> findByInspectionTransformerNoAndIsCurrentTrueOrderByCreatedAtDesc(String transformerNo);
    
    // Find all records (including history) for a transformer
    List<MaintenanceRecord> findByInspectionTransformerNoOrderByCreatedAtDesc(String transformerNo);
    
    // Find a specific version for an inspection
    Optional<MaintenanceRecord> findByInspectionIdAndVersion(Long inspectionId, Integer version);
    
    // Get the max version number for an inspection
    @Query("SELECT COALESCE(MAX(m.version), 0) FROM MaintenanceRecord m WHERE m.inspection.id = :inspectionId")
    Integer findMaxVersionByInspectionId(@Param("inspectionId") Long inspectionId);
    
    // Mark all records for an inspection as not current
    @Modifying
    @Query("UPDATE MaintenanceRecord m SET m.isCurrent = false WHERE m.inspection.id = :inspectionId")
    void markAllAsNotCurrentByInspectionId(@Param("inspectionId") Long inspectionId);
    
    // Legacy method (backward compatibility)
    Optional<MaintenanceRecord> findByInspectionId(Long inspectionId);
    List<MaintenanceRecord> findByInspectionTransformerNo(String transformerNo);
}