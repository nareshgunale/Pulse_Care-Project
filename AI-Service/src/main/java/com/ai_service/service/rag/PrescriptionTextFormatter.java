package com.ai_service.service.rag;

import com.ai_service.dto.MedicineDTO;
import com.ai_service.dto.PrescriptionDTO;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Converts structured PrescriptionDTO into a natural-language paragraph
 * so embeddings capture meaning properly (RAG works on sentences, not JSON).
 */
@Component
public class PrescriptionTextFormatter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    public String toReadableText(PrescriptionDTO p) {
        StringBuilder sb = new StringBuilder();

        sb.append("Prescription #").append(p.getId())
                .append(" dated ").append(p.getPrescriptionDate() != null ? p.getPrescriptionDate().format(DATE_FMT) : "unknown date")
                .append(", prescribed by Dr. ").append(p.getDoctorName() != null ? p.getDoctorName() : "unknown doctor")
                .append(" (appointment #").append(p.getAppointmentId()).append(").\n");

        List<MedicineDTO> medicines = p.getMedicines();
        if (medicines != null && !medicines.isEmpty()) {
            sb.append("Medicines prescribed: ");
            for (MedicineDTO m : medicines) {
                sb.append(m.getMedicineName())
                        .append(" (").append(m.getType()).append(", ").append(m.getRoutes()).append("), ")
                        .append(m.getDosage()).append(", ")
                        .append(m.getFrequency()).append(", for ")
                        .append(m.getDuration()).append(" days");
                if (m.getInstructions() != null && !m.getInstructions().isBlank()) {
                    sb.append(" — instructions: ").append(m.getInstructions());
                }
                sb.append(". ");
            }
            sb.append("\n");
        } else {
            sb.append("No medicines were prescribed.\n");
        }

        if (p.getPrescriptionNotes() != null && !p.getPrescriptionNotes().isBlank()) {
            sb.append("Doctor's notes: ").append(p.getPrescriptionNotes()).append("\n");
        }

        return sb.toString();
    }
}