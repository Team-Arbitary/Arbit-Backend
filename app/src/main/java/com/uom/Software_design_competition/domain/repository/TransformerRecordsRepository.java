package com.uom.Software_design_competition.domain.repository;

import com.uom.Software_design_competition.domain.entity.TransformerRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransformerRecordsRepository extends JpaRepository<TransformerRecords, Long>, JpaSpecificationExecutor<TransformerRecords> {
}
