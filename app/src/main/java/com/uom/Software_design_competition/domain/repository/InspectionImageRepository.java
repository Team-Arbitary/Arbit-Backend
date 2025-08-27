package com.uom.Software_design_competition.domain.repository;

import com.uom.Software_design_competition.domain.entity.InspectionImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionImageRepository extends JpaRepository<InspectionImage, Long> {
}