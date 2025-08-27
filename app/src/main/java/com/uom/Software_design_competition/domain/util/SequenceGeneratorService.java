package com.uom.Software_design_competition.domain.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

@Service
public class SequenceGeneratorService {

    @PersistenceContext
    private EntityManager entityManager;

    public long generateSequence(String sequenceName) {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('" + sequenceName + "')")
                .getSingleResult())
                .longValue();
    }
}