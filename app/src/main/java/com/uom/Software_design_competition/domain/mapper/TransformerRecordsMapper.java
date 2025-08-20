package com.uom.Software_design_competition.domain.mapper;

import com.uom.Software_design_competition.application.transport.request.TransformerRecordsRequest;
import com.uom.Software_design_competition.application.transport.response.TransformerRecordsResponse;
import com.uom.Software_design_competition.domain.entity.TransformerRecords;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TransformerRecordsMapper {

    public TransformerRecords mapRequestToEntityCreate(TransformerRecordsRequest request) {
        TransformerRecords transformerRecords = new TransformerRecords();
        transformerRecords.setRegions(request.getRegions());
        transformerRecords.setPoleNo(request.getPoleNo());
        transformerRecords.setTransformerNo(request.getTransformerNo());
        transformerRecords.setType(request.getType());
        transformerRecords.setLocation(request.getLocation());
        return transformerRecords;
    }

    public TransformerRecords mapRequestToEntityUpdate(TransformerRecords existingEntity, TransformerRecords request) {
        existingEntity.setRegions(request.getRegions());
        existingEntity.setPoleNo(request.getPoleNo());
        existingEntity.setTransformerNo(request.getTransformerNo());
        existingEntity.setType(request.getType());
        existingEntity.setLocation(request.getLocation());
        return existingEntity;
    }

    public TransformerRecordsResponse mapEntityToResponse(TransformerRecords entity) {
        TransformerRecordsResponse response = new TransformerRecordsResponse();
        response.setId(entity.getId());
        response.setRegions(entity.getRegions());
        response.setPoleNo(entity.getPoleNo());
        response.setTransformerNo(entity.getTransformerNo());
        response.setType(entity.getType());
        response.setLocation(entity.getLocation());

        return response;
    }
}