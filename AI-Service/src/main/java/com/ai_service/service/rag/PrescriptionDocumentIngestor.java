package com.ai_service.service.rag;

import com.ai_service.client.AppointmentClient;
import com.ai_service.dto.PrescriptionDTO;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Ingests a patient's prescriptions into Qdrant vector store —
 * one Document per prescription, tagged with patientId + prescriptionId metadata.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PrescriptionDocumentIngestor {

    private final AppointmentClient appointmentClient;
    private final PrescriptionTextFormatter formatter;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public void ingestForPatient(Long patientId) {

        List<PrescriptionDTO> prescriptions;
        try {
            prescriptions = appointmentClient.getPrescriptionsByPatientId(patientId);
        } catch (Exception e) {
            log.error("Failed to fetch prescriptions from AppointmentMS for patientId={}", patientId, e);
            throw e;
        }

        if (prescriptions == null || prescriptions.isEmpty()) {
            log.info("No prescriptions found for patientId={}", patientId);
            return;
        }

        int success = 0;
        for (PrescriptionDTO prescription : prescriptions) {
            try {
                ingestPrescription(prescription, patientId);
                success++;
            } catch (Exception e) {
                log.error("Skipping prescriptionId={} for patientId={} due to error",
                        prescription != null ? prescription.getId() : "null", patientId, e);
            }
        }

        log.info("Processed {}/{} prescriptions for patientId={}", success, prescriptions.size(), patientId);
    }

    public void ingestPrescription(PrescriptionDTO prescription, Long patientId) {

        if (prescription == null) {
            log.error("Received null prescription for patientId={}", patientId);
            throw new IllegalArgumentException("prescription is null");
        }

        String prescriptionId = String.valueOf(prescription.getId());
        log.info("Starting ingest: prescriptionId={}, patientId={}", prescriptionId, patientId);

        String text;
        try {
            text = formatter.toReadableText(prescription);
        } catch (Exception e) {
            log.error("Text formatting failed for prescriptionId={}, patientId={}", prescriptionId, patientId, e);
            throw e;
        }

        if (text == null || text.isBlank()) {
            log.error("Formatted text is empty for prescriptionId={}, patientId={}", prescriptionId, patientId);
            throw new IllegalStateException("Empty text for prescriptionId=" + prescriptionId);
        }

        TextSegment segment = TextSegment.from(text, dev.langchain4j.data.document.Metadata.from("patientId", String.valueOf(patientId))
                .put("prescriptionId", prescriptionId)
                .put("source", "database")
        );

        Response<Embedding> embeddingResponse;

        try {
            embeddingResponse = embeddingModel.embed(segment.text());

            int length = embeddingResponse.content().vector().length;

            log.info("INGESTION CHECK: Generated vector length = {}", length);

            if (length != 384) {
                throw new IllegalStateException("Vector length mismatch! Expected 384, got " + length);
            }

        } catch (Exception e) {
            log.error("embeddingModel.embed() FAILED for prescriptionId={}, patientId={}. " + "Check embedding model API key/config.", prescriptionId, patientId, e);
            throw e;
        }

        try {
            embeddingStore.add(embeddingResponse.content(), segment);
        } catch (Exception e) {
            log.error("embeddingStore.add() FAILED for prescriptionId={}, patientId={}. " + "Check Qdrant host/port/API key config.", prescriptionId, patientId, e);
            throw e;
        }
        log.info("Embedded prescriptionId={} for patientId={}", prescriptionId, patientId);
    }
}
