package com.uom.Software_design_competition.domain.repository;

import com.uom.Software_design_competition.domain.entity.InspectionRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InspectionRecordsRepository extends JpaRepository<InspectionRecords, Long> , JpaSpecificationExecutor<InspectionRecords> {
    List<InspectionRecords> findByTransformerNoOrderByDateOfInspectionDesc(String transformerNo);
    
    Optional<InspectionRecords> findByInspectionNo(String inspectionNo);

//    boolean existsByTransformerNoAndDateOfInspectionAndTime(String transformerNo,
//                                                            java.time.String dateOfInspection,
//                                                            java.time.String time);

//    List<InspectionRecords> findByTransformerNo(String transformerNo);
//
//    List<InspectionRecords> findByBatch(String batch);
//
//    boolean existsByTransformerNoAndDateOfInspectionAndTimeOfInspection(
//            String transformerNo,
//            java.time.String dateOfInspection,
//            java.time.String timeOfInspection);
}

