package com.hms.appointment.service;

import com.hms.appointment.dto.AppointmentDTO;
import com.hms.appointment.dto.AppointmentDetailsDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AppointmentService {
    Long scheduleAppointment(AppointmentDTO appointmentDTO);

    void cancelAppointment(Long appointmentId);

//    void completeAppointment(Long appointmentId);
//
//    void rescheduleAppointment(Long appointmentId, String newDateTime);

    AppointmentDTO getAppointmentDetails(Long appointmentId);

    AppointmentDetailsDTO getAppointmentDetailWithName(Long appointmentId);

    List<AppointmentDetailsDTO>getAllAppointmentByPatientId(Long patientId);

    List<AppointmentDetailsDTO>getAllAppointmentByDoctorId(Long doctorId);

    List<AppointmentDTO> getAllAppointments();

    List<AppointmentDTO> getAllAppointmentDetails();

    List<String> getAvailableSlots(Long doctorId, LocalDate date);

    Map<String, Object> getAllSlots(Long doctorId, LocalDate date);
}
