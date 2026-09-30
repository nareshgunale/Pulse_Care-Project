package com.hms.appointment.service.serviceImp;

import com.hms.appointment.clients.AiServiceClient;
import com.hms.appointment.entity.Prescription;
import com.hms.appointment.repository.PrescriptionRepository;
import com.hms.appointment.service.PrescriptionBackfillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrescriptionBackfillServiceImp implements PrescriptionBackfillService {

    private final PrescriptionRepository prescriptionRepository;
    private final AiServiceClient aiServiceClient;

    // Runs automatically ONE TIME when app starts.
    // Comment out @EventListener line below once backfill is confirmed done — no need to run again.
    @EventListener(ApplicationReadyEvent.class)
    @Async
    @Override
    public void backfillAllPatients() {

        try {
            Thread.sleep(15000); // give Eureka registry time to propagate
        } catch (InterruptedException ignored) {}

        Set<Long> patientIds = prescriptionRepository.findAll().stream()
                .map(Prescription::getPatientId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        log.info("Starting backfill for {} patients", patientIds.size());

        for (Long patientId : patientIds) {
            try {
                backfillForPatient(patientId);
            } catch (Exception e) {
                log.error("Backfill failed for patientId={}: {}", patientId, e.getMessage());
            }
        }

        log.info("Backfill job complete.");
    }

    @Override
    public void backfillForPatient(Long patientId) {
        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);

        if (prescriptions.isEmpty()) {
            log.info("No prescriptions for patientId={}, skipping", patientId);
            return;
        }

        log.info("Reindexing {} prescriptions for patientId={}", prescriptions.size(), patientId);

        // AI-Service pulls fresh data (DB + S3-resolved) via getPrescriptionsByPatientId
        aiServiceClient.reindexPrescriptions(patientId);
    }
}
