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
                .dateOfInspection(request.getDateOfInspection()) // Now expects String
                .time(request.getTime()) // Now expects String
                .maintenanceDate(request.getMaintenanceDate()) // Now expects String
                .status(request.getStatus() != null ? request.getStatus() : "Not started")
                .build();

        // Automatically set formatted maintenance date if not provided
        if (entity.getMaintenanceDate() == null) {
            setFormattedMaintenanceDate(entity);
        }

        return entity;
    }

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

        // Set maintenance date with current formatted date if needed
        // existingEntity.setMaintenanceDate(DATE_TIME_FORMAT.format(new Date()));

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
                entity.getStatus()
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