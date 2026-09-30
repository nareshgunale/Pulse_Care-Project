package com.hms.appointment.service;

import com.hms.appointment.dto.MedicineDTO;
import com.hms.appointment.dto.PrescriptionDTO;
import com.hms.appointment.exception.HMSException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class PrescriptionPdfService {

    private static final String APP_NAME = "PulseCare";
    private static final String APP_SUBTITLE = "Health Management System";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMMM yyyy");

    /**
     * Generates a real PDF from the supplied prescription data.
     *
     * @param prescription prescription information
     * @return PDF as byte array
     */
    public byte[] generatePrescriptionPdf(PrescriptionDTO prescription) {

        if (prescription == null) {
            throw new HMSException("PRESCRIPTION_NOT_FOUND");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, outputStream);
            document.open();
            addHeader(document);
            addPrescriptionDetails(document, prescription);
            addPatientInformation(document, prescription);
            addDoctorInformation(document, prescription);
            addDoctorNotes(document, prescription);
            addMedicinesTable(document, prescription);
            addDoctorSignature(document);
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF for prescription ID: {}", prescription.getId(), e);
            throw new HMSException("PRESCRIPTION_PDF_GENERATION_FAILED");
        }
    }

    /**
     * Adds PulseCare header.
     */
    private void addHeader(Document document) throws Exception {

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.BLACK);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
        Paragraph title = new Paragraph(APP_NAME, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);

        title.setSpacingAfter(2);
        Paragraph subtitle = new Paragraph(APP_SUBTITLE, subtitleFont);

        subtitle.setAlignment(Element.ALIGN_CENTER);

        subtitle.setSpacingAfter(15);

        document.add(title);
        document.add(subtitle);

        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(100);
        PdfPCell lineCell = new PdfPCell(new Phrase(""));
        lineCell.setBorder(Rectangle.BOTTOM);
        lineCell.setBorderWidthBottom(1f);
        lineCell.setPadding(0);
        lineTable.addCell(lineCell);
        document.add(lineTable);
        Paragraph space = new Paragraph(" ");
        space.setSpacingAfter(5);
        document.add(space);
    }

    /**
     * Adds prescription ID and date.
     */
    private void addPrescriptionDetails(Document document, PrescriptionDTO prescription) throws Exception {

        addSectionTitle(document, "Prescription Details");
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 1f});

        String prescriptionId = prescription.getId() != null ? "#" + prescription.getId() : "N/A";

        String prescriptionDate =
                prescription.getPrescriptionDate() != null
                        ? prescription
                        .getPrescriptionDate()
                        .format(DATE_FORMATTER)
                        : "N/A";

        addKeyValueCell(table, "Prescription ID", prescriptionId);
        addKeyValueCell(table, "Date", prescriptionDate);
        document.add(table);
    }

    /**
     * Adds patient information.
     * The current PrescriptionDTO only contains patientId,
     * so patientId is displayed here.
     */
    private void addPatientInformation(Document document, PrescriptionDTO prescription) throws Exception {
        addSectionTitle(document, "Patient Information");
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        addKeyValueCell(table, "Patient ID",
                prescription.getPatientId() != null
                        ? String.valueOf(
                        prescription.getPatientId()) : "N/A");
        document.add(table);
    }

    /**
     * Adds doctor information.
     */
    private void addDoctorInformation(Document document, PrescriptionDTO prescription) throws Exception {

        addSectionTitle(document, "Prescribed By");
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        String doctorName =
                prescription.getDoctorName() != null
                        && !prescription.getDoctorName().isBlank()
                        ? prescription.getDoctorName()
                        : "N/A";

        String doctorId = prescription.getDoctorId() != null ? String.valueOf(prescription.getDoctorId()) : "N/A";
        addKeyValueCell(table, "Doctor", doctorName);
        addKeyValueCell(table, "Doctor ID", doctorId);
        document.add(table);
    }

    /**
     * Adds doctor's prescription notes.
     */
    private void addDoctorNotes(Document document, PrescriptionDTO prescription) throws Exception {

        String notes = prescription.getPrescriptionNotes();

        if (notes == null || notes.isBlank()) {
            return;
        }

        addSectionTitle(document, "Doctor's Notes");
        Font notesFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

        Paragraph notesParagraph = new Paragraph(notes, notesFont);
        notesParagraph.setSpacingAfter(15);
        document.add(notesParagraph);
    }

    /**
     * Adds prescribed medicines table.
     */
    private void addMedicinesTable(Document document, PrescriptionDTO prescription) throws Exception {

        addSectionTitle(document, "Prescribed Medicines");
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.0f, 1.2f, 1.2f, 1.5f, 1.2f, 2.2f});
        table.setHeaderRows(1);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
        addTableHeader(table, "Medicine", headerFont);
        addTableHeader(table, "Type", headerFont);
        addTableHeader(table, "Dosage", headerFont);
        addTableHeader(table, "Frequency", headerFont);
        addTableHeader(table, "Duration", headerFont);
        addTableHeader(table, "Instructions", headerFont);

        if (prescription.getMedicines() != null
                && !prescription.getMedicines().isEmpty()) {

            for (MedicineDTO medicine :
                    prescription.getMedicines()) {

                addTableCell(table, medicine.getMedicineName(), bodyFont);
                addTableCell(table, medicine.getType(), bodyFont);
                addTableCell(table, medicine.getDosage(), bodyFont);
                addTableCell(table, medicine.getFrequency(), bodyFont);

                String duration =
                        medicine.getDuration() != null
                                ? medicine.getDuration()
                                + " days"
                                : "N/A";

                addTableCell(table, duration, bodyFont);
                addTableCell(table, medicine.getInstructions(), bodyFont);
            }

        } else {

            PdfPCell emptyCell = new PdfPCell(new Phrase("No medicines prescribed"));
            emptyCell.setColspan(6);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            emptyCell.setPadding(8);

            table.addCell(emptyCell);
        }

        document.add(table);
    }

    /**
     * Adds doctor signature area.
     */
    private void addDoctorSignature(Document document) throws Exception {

        Paragraph spacing = new Paragraph(" ");
        spacing.setSpacingBefore(45);
        document.add(spacing);
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        PdfPCell emptyCell = new PdfPCell(new Phrase(""));

        emptyCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell signatureCell = new PdfPCell(new Phrase("Doctor's Signature"));
        signatureCell.setBorder(Rectangle.NO_BORDER);
        signatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(emptyCell);
        table.addCell(signatureCell);
        document.add(table);
    }

    /**
     * Adds section title.
     */
    private void addSectionTitle(Document document, String title) throws Exception {

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);

        Paragraph paragraph = new Paragraph(title, font);

        paragraph.setSpacingBefore(8);
        paragraph.setSpacingAfter(8);

        document.add(paragraph);
    }

    /**
     * Adds key-value information.
     */
    private void addKeyValueCell(PdfPTable table, String key, String value) {

        Font keyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);

        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        Phrase phrase = new Phrase();
        phrase.add(new com.lowagie.text.Chunk(key + ": ", keyFont));
        phrase.add(new com.lowagie.text.Chunk(safeString(value), valueFont));

        PdfPCell cell = new PdfPCell(phrase);

        cell.setBorder(Rectangle.NO_BORDER);

        cell.setPadding(4);

        table.addCell(cell);
    }

    /**
     * Adds table header.
     */
    private void addTableHeader(PdfPTable table, String text, Font font) {

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setBackgroundColor(new Color(70, 70, 70));
        table.addCell(cell);
    }

    /**
     * Adds table body cell.
     */
    private void addTableCell(PdfPTable table, String value, Font font) {

        PdfPCell cell = new PdfPCell(new Phrase(safeString(value), font));
        cell.setPadding(5);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    /**
     * Converts null/blank strings to N/A.
     */
    private String safeString(String value) {

        if (value == null || value.isBlank()) {
            return "N/A";
        }
        return value;
    }
}