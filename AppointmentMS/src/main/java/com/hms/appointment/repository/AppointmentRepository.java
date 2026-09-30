package com.hms.appointment.repository;

import com.hms.appointment.constant.Status;
import com.hms.appointment.dto.AppointmentDetailsDTO;
import com.hms.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentTimeAndStatusNot(Long doctorId, LocalDateTime appointmentTime, Status status);

    List<Appointment> findByAppointmentTimeBetweenAndStatus(LocalDateTime from, LocalDateTime to, Status status);


    @Query("SELECT new com.hms.appointment.dto.AppointmentDetailsDTO(" +
            "a.id, a.patientId, a.doctorId, " +
            "a.appointmentTime, a.status, a.reason, a.notes) " +
            "FROM Appointment a WHERE a.patientId = ?1")
    List<AppointmentDetailsDTO> findAllByPatientId(Long patientId);

    @Query("SELECT new com.hms.appointment.dto.AppointmentDetailsDTO(" +
            "a.id, a.patientId, a.doctorId, " +
            "a.appointmentTime, a.status, a.reason, a.notes) " +
            "FROM Appointment a WHERE a.doctorId = ?1")
    List<AppointmentDetailsDTO> findAllByDoctorId(Long doctorId);
}
