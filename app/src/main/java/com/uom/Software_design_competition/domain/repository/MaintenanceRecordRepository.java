package com.uom.Software_design_competition.domain.repository;

import com.uom.Software_design_competition.domain.entity.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {
    Optional<MaintenanceRecord> findByInspectionId(Long inspectionId);
    List<MaintenanceRecord> findByInspectionTransformerNo(String transformerNo);
}
