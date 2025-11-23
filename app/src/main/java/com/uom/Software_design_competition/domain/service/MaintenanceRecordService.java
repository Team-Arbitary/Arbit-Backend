package com.uom.Software_design_competition.domain.service;

import com.uom.Software_design_competition.application.transport.request.MaintenanceRecordRequest;
import com.uom.Software_design_competition.application.transport.response.MaintenanceRecordResponse;
import com.uom.Software_design_competition.domain.entity.InspectionRecords;
import com.uom.Software_design_competition.domain.entity.MaintenanceRecord;
import com.uom.Software_design_competition.domain.repository.InspectionRecordsRepository;
import com.uom.Software_design_competition.domain.repository.MaintenanceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaintenanceRecordService {

    @Autowired
    private MaintenanceRecordRepository maintenanceRecordRepository;

    @Autowired
    private InspectionRecordsRepository inspectionRecordsRepository;

    @Transactional
    public MaintenanceRecordResponse createOrUpdateRecord(MaintenanceRecordRequest request) {
        InspectionRecords inspection = inspectionRecordsRepository.findById(request.getInspectionId())
                .orElseThrow(() -> new RuntimeException("Inspection not found"));

        MaintenanceRecord record = maintenanceRecordRepository.findByInspectionId(request.getInspectionId())
                .orElse(new MaintenanceRecord());

        record.setInspection(inspection);
        record.setInspectorName(request.getInspectorName());
        record.setStatus(request.getStatus());
        record.setVoltageReading(request.getVoltageReading());
        record.setCurrentReading(request.getCurrentReading());
        record.setRecommendedAction(request.getRecommendedAction());
        record.setRemarks(request.getRemarks());
        record.setReportData(request.getReportData());

        MaintenanceRecord savedRecord = maintenanceRecordRepository.save(record);
        return mapToResponse(savedRecord);
    }

    public MaintenanceRecordResponse getRecordByInspectionId(Long inspectionId) {
        return maintenanceRecordRepository.findByInspectionId(inspectionId)
                .map(this::mapToResponse)
                .orElse(null);
    }

    public List<MaintenanceRecordResponse> getRecordsByTransformerNo(String transformerNo) {
        return maintenanceRecordRepository.findByInspectionTransformerNo(transformerNo).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MaintenanceRecordResponse mapToResponse(MaintenanceRecord record) {
        return MaintenanceRecordResponse.builder()
                .id(record.getId())
                .inspectionId(record.getInspection().getId())
                .transformerNo(record.getInspection().getTransformerNo())
                .inspectorName(record.getInspectorName())
                .status(record.getStatus())
                .voltageReading(record.getVoltageReading())
                .currentReading(record.getCurrentReading())
                .recommendedAction(record.getRecommendedAction())
                .remarks(record.getRemarks())
                .reportData(record.getReportData())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
