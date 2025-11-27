package com.uom.Software_design_competition.domain.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.uom.Software_design_competition.domain.entity.InspectionRecords;
import com.uom.Software_design_competition.domain.entity.MaintenanceRecord;
import com.uom.Software_design_competition.domain.repository.InspectionRecordsRepository;
import com.uom.Software_design_competition.domain.repository.MaintenanceRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;

@Slf4j
@Service
public class PdfReportService{

	@Autowired
	private MaintenanceRecordRepository maintenanceRecordRepository;

	@Autowired
	private InspectionRecordsRepository inspectionRecordsRepository;

	private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
	private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
	private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
	private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

	public byte[] generateMaintenanceRecordPdf(Long recordId) {
		try {
			MaintenanceRecord record = maintenanceRecordRepository.findById(recordId)
					.orElseThrow(() -> new RuntimeException("Maintenance record not found"));

			Document document = new Document(PageSize.A4);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);

			document.open();

			// Title
			Paragraph title = new Paragraph("Maintenance Record", TITLE_FONT);
			title.setAlignment(Element.ALIGN_CENTER);
			title.setSpacingAfter(20);
			document.add(title);

			// Transformer Info
			document.add(new Paragraph("Transformer: " + record.getInspection().getTransformerNo(), HEADER_FONT));
			document.add(new Paragraph("Inspection No: " + record.getInspection().getInspectionNo(), NORMAL_FONT));
			document.add(new Paragraph(" "));

			// Maintenance Details Section
			addMaintenanceDetails(document, record);

			// Gang Composition Section
			addGangComposition(document, record);

			// Inspection Sign-offs Section
			addInspectionSignoffs(document, record);

			document.close();
			return baos.toByteArray();

		} catch (Exception ex) {
			log.error("Error generating maintenance record PDF", ex);
			throw new RuntimeException("Failed to generate maintenance record PDF", ex);
		}
	}

	public byte[] generateThermalInspectionPdf(Long inspectionId) {
		try {
			InspectionRecords inspection = inspectionRecordsRepository.findById(inspectionId)
					.orElseThrow(() -> new RuntimeException("Inspection not found"));

			Document document = new Document(PageSize.A4, 36, 36, 50, 50);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);

			document.open();

			// Title
			Paragraph title = new Paragraph("Thermal Image Inspection Form", TITLE_FONT);
			title.setAlignment(Element.ALIGN_CENTER);
			title.setSpacingAfter(20);
			document.add(title);

			// Branch and Transformer Info
			addBranchInfo(document, inspection);

			// Date of Inspection
			addInspectionDate(document, inspection);

			// Thermal Images
			addThermalImages(document, inspection);

			// Engineer Provided Information
			addEngineerInformation(document, inspection);

			document.close();
			return baos.toByteArray();

		} catch (Exception ex) {
			log.error("Error generating thermal inspection PDF", ex);
			throw new RuntimeException("Failed to generate thermal inspection PDF", ex);
		}
	}

	private void addMaintenanceDetails(Document document, MaintenanceRecord record) throws DocumentException {
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		addTableHeader(table, "Maintenance Details");

		addTableRow(table, "Start Time:", record.getStartTime());
		addTableRow(table, "Completion Time:", record.getCompletionTime());
		addTableRow(table, "Supervised By:", record.getSupervisedBy());

		document.add(table);
	}

	private void addGangComposition(Document document, MaintenanceRecord record) throws DocumentException {
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		addTableHeader(table, "Gang Composition");

		addTableRow(table, "Tech I:", record.getTechI());
		addTableRow(table, "Tech II:", record.getTechII());
		addTableRow(table, "Tech III:", record.getTechIII());
		addTableRow(table, "Helpers:", record.getHelpers());

		document.add(table);
	}

	private void addInspectionSignoffs(Document document, MaintenanceRecord record) throws DocumentException {
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		addTableHeader(table, "Inspection Sign-offs");

		addTableRow(table, "Inspected By:", record.getInspectedBy());
		addTableRow(table, "Inspected Date:", record.getInspectedByDate());
		addTableRow(table, "Rectified By:", record.getRectifiedBy());
		addTableRow(table, "Rectified Date:", record.getRectifiedByDate());
		addTableRow(table, "Re-Inspected By:", record.getReInspectedBy());
		addTableRow(table, "Re-Inspected Date:", record.getReInspectedByDate());
		addTableRow(table, "CSS:", record.getCss());
		addTableRow(table, "CSS Date:", record.getCssDate());

		document.add(table);
	}

	private void addBranchInfo(Document document, InspectionRecords inspection) throws DocumentException {
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		addCell(table, "Branch:", HEADER_FONT);
		addCell(table, getStringValue(inspection.getBranch()), NORMAL_FONT);
		addCell(table, "Transformer No:", HEADER_FONT);
		addCell(table, getStringValue(inspection.getTransformerNo()), NORMAL_FONT);

		document.add(table);
	}

	private void addInspectionDate(Document document, InspectionRecords inspection) throws DocumentException {
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		addCell(table, "Date of Inspection:", HEADER_FONT);
		addCell(table, getStringValue(inspection.getDateOfInspection()), NORMAL_FONT);
		addCell(table, "Time:", HEADER_FONT);
		addCell(table, getStringValue(inspection.getTime()), NORMAL_FONT);

		document.add(table);
	}

	private void addThermalImages(Document document, InspectionRecords inspection) throws DocumentException {
		document.add(new Paragraph("Thermal Images:", HEADER_FONT));
		document.add(new Paragraph(" "));

		PdfPTable imageTable = new PdfPTable(3);
		imageTable.setWidthPercentage(100);
		imageTable.setSpacingBefore(10);
		imageTable.setSpacingAfter(10);

		// Add image labels
		addCell(imageTable, "Left", HEADER_FONT);
		addCell(imageTable, "Right", HEADER_FONT);
		addCell(imageTable, "Front", HEADER_FONT);

		// Add images
		addImageCell(imageTable, inspection.getIrLeft());
		addImageCell(imageTable, inspection.getIrRight());
		addImageCell(imageTable, inspection.getIrFront());

		document.add(imageTable);
	}

	private void addEngineerInformation(Document document, InspectionRecords inspection) throws DocumentException {
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setSpacingBefore(15);
		table.setSpacingAfter(10);

		addTableHeader(table, "Engineer Information");

		addTableRow(table, "Inspector Name:", inspection.getInspectorName());
		addTableRow(table, "Status:", inspection.getEngineerStatus());
		addTableRow(table, "Voltage Reading:", inspection.getVoltage());
		addTableRow(table, "Current Reading:", inspection.getCurrent());
		addTableRow(table, "Recommended Action:", inspection.getRecommendedAction());
		addTableRow(table, "Additional Remarks:", inspection.getAdditionalRemarks());

		document.add(table);
	}

	private void addTableHeader(PdfPTable table, String headerText) {
		PdfPCell cell = new PdfPCell(new Phrase(headerText, HEADER_FONT));
		cell.setColspan(2);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		cell.setPadding(8);
		table.addCell(cell);
	}

	private void addTableRow(PdfPTable table, String label, String value) {
		addCell(table, label, HEADER_FONT);
		addCell(table, getStringValue(value), NORMAL_FONT);
	}

	private void addCell(PdfPTable table, String text, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setPadding(5);
		table.addCell(cell);
	}

	private void addImageCell(PdfPTable table, String imagePath) {
		try {
			if (imagePath != null && !imagePath.isEmpty()) {
				File imgFile = new File(imagePath);
				if (imgFile.exists()) {
					Image img = Image.getInstance(imagePath);
					img.scaleToFit(150, 150);
					PdfPCell cell = new PdfPCell(img);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPadding(5);
					cell.setMinimumHeight(160);
					table.addCell(cell);
					return;
				}
			}
		} catch (Exception e) {
			log.warn("Could not load image: " + imagePath, e);
		}
		// Add placeholder if image not available
		PdfPCell cell = new PdfPCell(new Phrase("[Image Not Available]", SMALL_FONT));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(5);
		cell.setMinimumHeight(160);
		table.addCell(cell);
	}

	private String getStringValue(Object value) {
		return value != null ? value.toString() : "-";
	}
}