package com.hms.appointment.controller;


import com.hms.appointment.service.PrescriptionBackfillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin-only trigger for the one-time historical prescription backfill.
 * secure this behind admin auth / internal network before prod use.
 */
@RestController
@RequiredArgsConstructor
public class PrescriptionBackfillController {

    private final PrescriptionBackfillService backfillService;

    @PostMapping("/admin/rag/backfill-all")
    public String backfillAll() {
        backfillService.backfillAllPatients();
        return "Backfill job started in background for all patients";
    }

    @PostMapping("/admin/rag/backfill/{patientId}")
    public String backfillOne(@PathVariable Long patientId) {
        backfillService.backfillForPatient(patientId);
        return "Backfill triggered for patient " + patientId;
    }
}