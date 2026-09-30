package com.ai_service.controller;

import com.ai_service.service.rag.PrescriptionDocumentIngestor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal endpoint — called by Appointment MS after a prescription is
 * saved. Secure behind internal network / API key in production.
 */
@RestController
@RequiredArgsConstructor
public class RagIngestController {

    private final PrescriptionDocumentIngestor ingestor;

    @PostMapping("/ai/rag/ingest-prescriptions/{patientId}")
    public ResponseEntity<String> ingestPrescriptions(@PathVariable Long patientId) {
        ingestor.ingestForPatient(patientId);
        return ResponseEntity.ok("Successfully ingested prescriptions for patientId: " + patientId);
    }
}