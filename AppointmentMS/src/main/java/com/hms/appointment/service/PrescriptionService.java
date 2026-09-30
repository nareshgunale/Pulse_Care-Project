package com.hms.appointment.service;

import com.hms.appointment.dto.PrescriptionDTO;

import java.util.List;

public interface PrescriptionService {

    Long savePrescription(PrescriptionDTO request);

    PrescriptionDTO getPrescriptionByAppointmentId(Long appointmentId);

    PrescriptionDTO getPrescriptionById(Long prescriptionId);
    void deleteByAppointmentId(Long appointmentId);

    List<PrescriptionDTO> getAllByDoctorId(Long doctorId);

    List<PrescriptionDTO> getAllByPatientId(Long patientId);
}
