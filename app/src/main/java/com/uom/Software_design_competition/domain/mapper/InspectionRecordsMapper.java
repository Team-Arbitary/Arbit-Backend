package com.uom.Software_design_competition.domain.mapper;

import com.uom.Software_design_competition.application.transport.request.InspectionRecordsRequest;
import com.uom.Software_design_competition.application.transport.response.InspectionRecordsResponse;
import com.uom.Software_design_competition.domain.entity.InspectionRecords;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class InspectionRecordsMapper {

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("EEE, MMM dd, yyyy hh:mm a");

    public InspectionRecords mapRequestToEntityCreate(InspectionRecordsRequest request) {
        InspectionRecords entity = InspectionRecords.builder()
                .branch(request.getBranch())
                .transformerNo(request.getTransformerNo())
                .dateOfInspection(request.getDateOfInspection()) // expects String
                .time(request.getTime()) // expects String
                .maintenanceDate(request.getMaintenanceDate()) // expects String
                .status(request.getStatus() != null ? request.getStatus() : "Not started")
                .inspectorName(request.getInspectorName())
                .engineerStatus(request.getEngineerStatus())
                .voltage(request.getVoltage())
                .current(request.getCurrent())
                .recommendedAction(request.getRecommendedAction())
                .additionalRemarks(request.getAdditionalRemarks())
                .irLeft(request.getIrLeft())
                .irRight(request.getIrRight())
                .irFront(request.getIrFront())
                .build();

        // Automatically set formatted maintenance date if not provided
        if (entity.getMaintenanceDate() == null) {
            setFormattedMaintenanceDate(entity);
        }

        return entity;
    }

    /**
     * Update existing entity fields from the provided updateRequest entity.
     * updateRequest is expected to be an InspectionRecords instance containing new values.
     */
    public InspectionRecords mapRequestToEntityUpdate(InspectionRecords existingEntity, InspectionRecords updateRequest) {
        if (updateRequest.getBranch() != null) {
            existingEntity.setBranch(updateRequest.getBranch());
        }
        if (updateRequest.getTransformerNo() != null) {
            existingEntity.setTransformerNo(updateRequest.getTransformerNo());
        }
        if (updateRequest.getDateOfInspection() != null) {
            existingEntity.setDateOfInspection(updateRequest.getDateOfInspection());
        }
        if (updateRequest.getTime() != null) {
            existingEntity.setTime(updateRequest.getTime());
        }
        if (updateRequest.getMaintenanceDate() != null) {
            existingEntity.setMaintenanceDate(updateRequest.getMaintenanceDate());
        }
        if (updateRequest.getStatus() != null) {
            existingEntity.setStatus(updateRequest.getStatus());
        }

        // Engineer fields
        if (updateRequest.getInspectorName() != null) {
            existingEntity.setInspectorName(updateRequest.getInspectorName());
        }
        if (updateRequest.getEngineerStatus() != null) {
            existingEntity.setEngineerStatus(updateRequest.getEngineerStatus());
        }
        if (updateRequest.getVoltage() != null) {
            existingEntity.setVoltage(updateRequest.getVoltage());
        }
        if (updateRequest.getCurrent() != null) {
            existingEntity.setCurrent(updateRequest.getCurrent());
        }
        if (updateRequest.getRecommendedAction() != null) {
            existingEntity.setRecommendedAction(updateRequest.getRecommendedAction());
        }
        if (updateRequest.getAdditionalRemarks() != null) {
            existingEntity.setAdditionalRemarks(updateRequest.getAdditionalRemarks());
        }

        // Thermal images
        if (updateRequest.getIrLeft() != null) {
            existingEntity.setIrLeft(updateRequest.getIrLeft());
        }
        if (updateRequest.getIrRight() != null) {
            existingEntity.setIrRight(updateRequest.getIrRight());
        }
        if (updateRequest.getIrFront() != null) {
            existingEntity.setIrFront(updateRequest.getIrFront());
        }

        return existingEntity;
    }

    public InspectionRecordsResponse mapEntityToResponse(InspectionRecords entity) {
        // Combine date and time for inspection display
        String combinedInspectionDateTime = null;
        if (entity.getDateOfInspection() != null && entity.getTime() != null) {
            combinedInspectionDateTime = entity.getDateOfInspection() + " " + entity.getTime();
        } else if (entity.getDateOfInspection() != null) {
            combinedInspectionDateTime = entity.getDateOfInspection();
        }

        return new InspectionRecordsResponse(
                entity.getId(),
                entity.getInspectionNo(),
                entity.getBranch(),
                entity.getTransformerNo(),
                combinedInspectionDateTime, // Combined date and time
                entity.getTime(), // Original time field
                entity.getMaintenanceDate(),
                entity.getStatus(),

                // Engineer fields
                entity.getInspectorName(),
                entity.getEngineerStatus(),
                entity.getVoltage(),
                entity.getCurrent(),
                entity.getRecommendedAction(),
                entity.getAdditionalRemarks(),

                // Thermal images
                entity.getIrLeft(),
                entity.getIrRight(),
                entity.getIrFront()
        );
    }

    // Method to set formatted maintenance date
    public void setFormattedMaintenanceDate(InspectionRecords entity) {
        entity.setMaintenanceDate(DATE_TIME_FORMAT.format(new Date()));
    }

    // Method to get current formatted date time
    public String getCurrentFormattedDateTime() {
        return DATE_TIME_FORMAT.format(new Date());
    }
}