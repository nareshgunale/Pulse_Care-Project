package com.hms.appointment.service;

public interface PrescriptionBackfillService {

     void backfillAllPatients();
    void backfillForPatient(Long patientId);
}
