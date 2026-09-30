package com.hms.appointment.service.serviceImp;

import com.hms.appointment.constant.Status;
import com.hms.appointment.clients.ProfileClients;
import com.hms.appointment.dto.AppointmentDTO;
import com.hms.appointment.dto.AppointmentDetailsDTO;
import com.hms.appointment.dto.DoctorDTO;
import com.hms.appointment.dto.PatientDTO;
import com.hms.appointment.entity.Appointment;
import com.hms.appointment.entity.DoctorSchedule;
import com.hms.appointment.exception.HMSException;
import com.hms.appointment.repository.AppointmentRepository;
import com.hms.appointment.repository.DoctorScheduleRepository;
import com.hms.appointment.service.ApiService;
import com.hms.appointment.service.AppointmentService;
import com.hms.appointment.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImp implements AppointmentService {


    private final AppointmentRepository appointmentRepository;

    private final ProfileClients profileClients;

    private final DoctorScheduleRepository doctorScheduleRepository;

    private final WhatsAppService whatsAppService;


    @Override
    public Long scheduleAppointment(AppointmentDTO appointmentDTO) {
        Boolean doctorExists = profileClients.doctorExists(appointmentDTO.getDoctorId());
        if (doctorExists == null || !doctorExists) {
            throw new HMSException("DOCTOR_NOT_FOUND");
        }
        Boolean patientExists = profileClients.patientExists(appointmentDTO.getPatientId());
        if (patientExists == null || !patientExists) {
            throw new HMSException("PATIENT_NOT_FOUND");
        }

        LocalDateTime appointmentTime = appointmentDTO.getAppointmentTime();
        Long doctorId = appointmentDTO.getDoctorId();

        //check already doctor appointment book or not
        DayOfWeek day = appointmentTime.getDayOfWeek();
        DoctorSchedule schedule = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, day)
                .orElseThrow(() -> new HMSException("DOCTOR_SCHEDULE_NOT_FOUND"));

        //check doctor available or not on that day
        if (!schedule.isAvailable()) {
            throw new HMSException("DOCTOR_NOT_AVAILABLE_ON_THIS_DAY");
        }

        //check working hours
        LocalTime requestedTime = appointmentTime.toLocalTime();
        if (requestedTime.isBefore(schedule.getStartTime()) ||
                requestedTime.isAfter(schedule.getEndTime())) {
            throw new HMSException("APPOINTMENT_TIME_OUT_OF_SCHEDULE");
        }

        //check same doctor same time available or not
        boolean alreadyBooked = appointmentRepository
                .existsByDoctorIdAndAppointmentTimeAndStatusNot(doctorId, appointmentTime,Status.CANCELLED);
        if (alreadyBooked) {
            throw new HMSException("SLOT_ALREADY_BOOKED");
        }

        appointmentDTO.setStatus(Status.SCHEDULED);
        Long appointmentId = appointmentRepository.save(appointmentDTO.toEntity()).getId();

        try {
            PatientDTO patient = profileClients.getPatientById(appointmentDTO.getPatientId());
            DoctorDTO doctor = profileClients.getDoctorById(appointmentDTO.getDoctorId());

            if (patient != null && patient.getPhoneNo() != null) {
                whatsAppService.sendMessage(
                        patient.getPhoneNo(),
                        "*Appointment Confirmed!*\n\n" +
                                "Dear *" + patient.getName() + "*,\n\n" +
                                "Your appointment has been successfully booked! 🎉\n\n" +
                                "*Doctor:* Dr. " + (doctor != null ? doctor.getName() : "") + "\n" +
                                "*Hospital:* " + (doctor != null ? doctor.getHospitalName() : "") + "\n" +
                                "*Location:* " + (doctor != null ? doctor.getCity() : "") + "\n" +
                                "*Date & Time:* " + appointmentDTO.getAppointmentTime() + "\n\n" +
                                "Please arrive 10 minutes early.\n\n" +
                                "_— PulseCare Team_"
                );
            }
        } catch (Exception e) {
            System.out.println("WhatsApp notification failed: " + e.getMessage());
        }

        return appointmentId;

    }

    @Override
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() ->
                new HMSException("APPOINTMENT_NOT_FOUND"));
        if (appointment.getStatus().equals(Status.CANCELLED)) {
            throw new HMSException("APPOINTMENT_ALREADY_CANCELLED");
        }
        appointment.setStatus(Status.CANCELLED);
        appointmentRepository.save(appointment);
    }

//    @Override
//    public void completeAppointment(Long appointmentId) {
//
//    }
//
//    @Override
//    public void rescheduleAppointment(Long appointmentId, String newDateTime) {
//
//    }

    @Override
    public AppointmentDTO getAppointmentDetails(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new HMSException("APPOINTEMENT_NOT_FOUND"))
                .toDto();
    }

    @Override
    public AppointmentDetailsDTO getAppointmentDetailWithName(Long appointmentId) {
        AppointmentDTO appointmentDTO = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new HMSException("APPOINTMENT_NOT_FOUND"))
                .toDto();

        DoctorDTO doctorDTO = profileClients.getDoctorById(appointmentDTO.getDoctorId());
        PatientDTO patientDTO = profileClients.getPatientById(appointmentDTO.getPatientId());

        if (patientDTO == null) throw new HMSException("PATIENT_NOT_FOUND");
        if (doctorDTO == null) throw new HMSException("DOCTOR_NOT_FOUND");

        return new AppointmentDetailsDTO(
                appointmentDTO.getId(),
                appointmentDTO.getPatientId(),
                patientDTO.getName(),
                patientDTO.getEmail(),
                patientDTO.getPhoneNo(),
                appointmentDTO.getDoctorId(),
                doctorDTO.getName(),
                doctorDTO.getHospitalName(),
                doctorDTO.getCity(),
                appointmentDTO.getAppointmentTime(),
                appointmentDTO.getStatus(),
                appointmentDTO.getReason(),
                appointmentDTO.getNotes()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppointmentDetailsDTO> getAllAppointmentByPatientId(Long patientId) {
        return appointmentRepository.findAllByPatientId(patientId).stream()
                .map(appointment -> {
                    DoctorDTO doctorDTO =
                            profileClients.getDoctorById(appointment.getDoctorId());//n+1 problem
                    appointment.setDoctorName(doctorDTO.getName());
                    return appointment;
                }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppointmentDetailsDTO> getAllAppointmentByDoctorId(Long doctorId) {
        return appointmentRepository.findAllByDoctorId(doctorId).stream()
                .map(a->{
                     PatientDTO patientById = profileClients.getPatientById(a.getPatientId());
//                     a.setPatientName(patientById.getName());
                    if (patientById != null){
                        a.setPatientName(patientById.getName());
                    }else {
                        a.setPatientName("Inactive Patient");
                    }
                     return a;
                }).toList();
    }

//    @Transactional(readOnly = true)
//    @Override
//    public List<AppointmentDetailsDTO> getAllAppointmentByDoctorId(Long doctorId) {
//        List<Appointment> appointments =
//                appointmentRepository.findAllByDoctorId(doctorId);
//        if (appointments.isEmpty()) {
//            return List.of();
//        }
//        List<Long> patientIds = appointments.stream()
//                .map(Appointment::getPatientId)
//                .distinct()
//                .toList();
//        if (patientIds.isEmpty()) {
//            return List.of();
//        }
//        List<PatientDTO> patients = profileClients.getPatientsByIds(patientIds);
//        if (patients == null) {
//            patients = List.of();
//        }
//        Map<Long, PatientDTO> patientMap = patients.stream()
//                .collect(Collectors.toMap(PatientDTO::getId, p -> p));
//        return appointments.stream()
//                .map(app -> {
//                    PatientDTO patient = patientMap.get(app.getPatientId());
//                    if (patient == null) {
//                        throw new HMSException("PATIENT_NOT_FOUND");
//                    }
//                    return new AppointmentDetailsDTO(
//                            app.getId(),
//                            app.getPatientId(),
//                            patient.getName(),
//                            patient.getEmail(),
//                            patient.getPhoneNo(),
//                            app.getDoctorId(),
//                            null,
//                            app.getAppointmentTime(),
//                            app.getStatus(),
//                            app.getReason(),
//                            app.getNotes()
//                    );
//                })
//                .toList();
//    }

    @Override
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(Appointment::toDto)
                .toList();
    }

    @Override
    public List<AppointmentDTO> getAllAppointmentDetails() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .map(Appointment::toDto)
                .toList();
    }

    @Override
    public List<String> getAvailableSlots(Long doctorId, LocalDate date) {
        DoctorSchedule schedule = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, date.getDayOfWeek())
                .orElseThrow(() -> new HMSException("DOCTOR_SCHEDULE_NOT_FOUND"));

        if (!schedule.isAvailable()) {
            return List.of();
        }

        List<String> slots = new ArrayList<>();
        LocalTime current = schedule.getStartTime();
        while (current.isBefore(schedule.getEndTime())) {
            LocalDateTime slotDateTime = LocalDateTime.of(date, current);
            boolean booked = appointmentRepository.existsByDoctorIdAndAppointmentTimeAndStatusNot(doctorId, slotDateTime,Status.CANCELLED);
            if (!booked) {
                slots.add(current.toString());
            }
            current = current.plusMinutes(30);
        }
        return slots;
    }

    @Override
    public Map<String, Object> getAllSlots(Long doctorId, LocalDate date) {
        DoctorSchedule schedule = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, date.getDayOfWeek())
                .orElseThrow(() -> new HMSException("DOCTOR_SCHEDULE_NOT_FOUND"));

        if (!schedule.isAvailable()) {
            return Map.of("available", List.of(), "booked", List.of());
        }

        List<String> available = new ArrayList<>();
        List<String> booked = new ArrayList<>();

        LocalTime current = schedule.getStartTime();
        while (current.isBefore(schedule.getEndTime())) {
            LocalDateTime slotDateTime = LocalDateTime.of(date, current);
            boolean isBooked = appointmentRepository.existsByDoctorIdAndAppointmentTimeAndStatusNot(doctorId, slotDateTime,Status.CANCELLED);
            if (isBooked) {
                booked.add(current.toString());
            } else {
                available.add(current.toString());
            }
            current = current.plusMinutes(30);
        }

        return Map.of("available", available, "booked", booked);
    }
}
