package com.hms.appointment.service;

import com.hms.appointment.dto.AppointmentRecordDTO;
import com.hms.appointment.dto.RecordDetailsDTO;

import java.util.List;

public interface AppointmentRecordService {

    Long createAppointmentRecord(AppointmentRecordDTO request);
    void updateAppointmentRecord(AppointmentRecordDTO request);
    AppointmentRecordDTO getAppointmentRecordByAppointmentId(Long appointmentId);
    AppointmentRecordDTO getAppointmentRecordDetailByAppointmentId(Long appointmentId);
    AppointmentRecordDTO getAppointmentRecordById(Long recordId);
    void deleteAppointmentRecord(Long recordId);
    List<RecordDetailsDTO>getAppointmentRecordByPatientId(Long patientId);
}
