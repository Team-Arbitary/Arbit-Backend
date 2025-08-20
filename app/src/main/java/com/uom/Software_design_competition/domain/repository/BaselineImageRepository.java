package com.uom.Software_design_competition.domain.repository;

import com.uom.Software_design_competition.domain.entity.BaselineImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaselineImageRepository extends JpaRepository<BaselineImage, Long> {
}