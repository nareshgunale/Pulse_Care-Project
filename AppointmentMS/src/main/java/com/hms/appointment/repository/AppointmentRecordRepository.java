package com.hms.appointment.repository;

import com.hms.appointment.entity.AppointmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRecordRepository extends JpaRepository<AppointmentRecord, Long> {
    Optional<AppointmentRecord> findByAppointment_Id(Long appointmentId);

    List<AppointmentRecord> findByPatientId(Long patientId);
}
