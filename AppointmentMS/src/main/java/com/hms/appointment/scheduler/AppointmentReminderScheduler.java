package com.hms.appointment.scheduler;

import com.hms.appointment.clients.ProfileClients;
import com.hms.appointment.constant.Status;
import com.hms.appointment.dto.DoctorDTO;
import com.hms.appointment.dto.PatientDTO;
import com.hms.appointment.entity.Appointment;
import com.hms.appointment.repository.AppointmentRepository;
import com.hms.appointment.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AppointmentReminderScheduler {

    private final AppointmentRepository appointmentRepository;
    private final WhatsAppService whatsAppService;
    private final ProfileClients profileClients;

    @Scheduled(fixedRate = 300000)
    public void sendReminders() {
        LocalDateTime from = LocalDateTime.now().plusMinutes(28);
        LocalDateTime to = LocalDateTime.now().plusMinutes(32);

        List<Appointment> upcoming = appointmentRepository
                .findByAppointmentTimeBetweenAndStatus(from, to, Status.SCHEDULED);

        for (Appointment appt : upcoming) {
            try {
                PatientDTO patient = profileClients.getPatientById(appt.getPatientId());
                DoctorDTO doctor = profileClients.getDoctorById(appt.getDoctorId());

                if (patient != null && patient.getPhoneNo() != null) {
                    whatsAppService.sendMessage(
                            patient.getPhoneNo(),
                            "⏰ *Appointment Reminder!*\n\n" +
                                    "Dear *" + patient.getName() + "*,\n\n" +
                                    "Your appointment is in *30 minutes!* ⏳\n\n" +
                                    "👨‍⚕️ *Doctor:* Dr. " + (doctor != null ? doctor.getName() : "") + "\n" +
                                    "📅 *Time:* " + appt.getAppointmentTime() + "\n\n" +
                                    "Please start heading to the hospital now.\n\n" +
                                    "_— PulseCare Team_ 🏥"
                    );
                }
            } catch (Exception e) {
                System.out.println("Reminder failed: " + e.getMessage());
            }
        }
    }
}