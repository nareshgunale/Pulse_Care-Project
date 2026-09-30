package com.hms.appointment.service.serviceImp;

import com.hms.appointment.clients.AiServiceClient;
import com.hms.appointment.clients.ProfileClients;
import com.hms.appointment.constant.Status;
import com.hms.appointment.dto.AppointmentRecordDTO;
import com.hms.appointment.dto.DoctorName;
import com.hms.appointment.dto.RecordDetailsDTO;
import com.hms.appointment.entity.Appointment;
import com.hms.appointment.entity.AppointmentRecord;
import com.hms.appointment.exception.HMSException;
import com.hms.appointment.repository.AppointmentRecordRepository;
import com.hms.appointment.repository.AppointmentRepository;
import com.hms.appointment.service.AppointmentRecordService;
import com.hms.appointment.service.PrescriptionService;
import com.hms.appointment.service.S3ArchiveService;
import com.hms.appointment.utility.StringListConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AppointmentRecordServiceImp implements AppointmentRecordService {

    private final AppointmentRecordRepository appointmentRecordRepository;

    private final PrescriptionService prescriptionService;

    private final ProfileClients profileClients;

    private final AppointmentRepository appointmentRepository;

    private final S3ArchiveService s3ArchiveService;

    private final AiServiceClient aiServiceClient;

    ///  Create new appointment record
    @Override
    public Long createAppointmentRecord(AppointmentRecordDTO request) {
        Optional<AppointmentRecord> existingRecord =
                appointmentRecordRepository.findByAppointment_Id(request.getAppointmentId());
        if (existingRecord.isPresent()) {
            throw new HMSException("APPOINTMENT_RECORD_ALREADY_EXISTS");
        }
        request.setCreatedAt(LocalDateTime.now());
        Long id = appointmentRecordRepository.save(request.toEntity()).getId();
        if (request.getPrescription() != null) {
            /// Set appointment id inside prescription
            request.getPrescription().setAppointmentId(request.getAppointmentId());
            request.getPrescription().setDoctorId(request.getDoctorId());

            request.getPrescription().setPatientId(request.getPatientId());
            /// Save prescription
            prescriptionService.savePrescription(request.getPrescription());

            /// trigger RAG. Never breaks the main save flow.
            try {
                aiServiceClient.reindexPrescriptions(request.getPatientId());
            } catch (Exception e) {
                log.warn("RAG failed for patientId={}: {}", request.getPatientId(), e.getMessage());
            }
        }
        // UPDATE APPOINTMENT STATUS
        Appointment appointment = appointmentRepository
                .findById(request.getAppointmentId())
                .orElseThrow(() ->  
                        new HMSException("APPOINTMENT_NOT_FOUND"));
        appointment.setStatus(Status.COMPLETED);
        appointmentRepository.save(appointment);

        return id;
    }

    /// Update existing appointment record
    @Override
    public void updateAppointmentRecord(AppointmentRecordDTO request) {

        // Find existing record by id
        AppointmentRecord existingRecord = appointmentRecordRepository.findById(request.getId())
                .orElseThrow(() -> new HMSException("APPOINTMENT_RECORD_NOT_FOUND"));
        existingRecord.setNotes(request.getNotes());
        existingRecord.setDiagnosis(request.getDiagnosis());
        existingRecord.setFollowUpDate(request.getFollowUpDate());
        existingRecord.setSymptoms(
                StringListConverter.convertListToString(request.getSymptoms())
        );
        existingRecord.setTests(
                StringListConverter.convertListToString(request.getTests())
        );
        existingRecord.setReferrals(request.getReferrals());
    }

    /// Get appointment record using appointment id
    @Override
    public AppointmentRecordDTO getAppointmentRecordByAppointmentId(Long appointmentId) {

        return appointmentRecordRepository.findByAppointment_Id(appointmentId)
                .orElseThrow(() -> new HMSException("APPOINTMENT_RECORD_NOT_FOUND"))
                .toDTO();
    }

    /// Get appointment record with prescription details
    @Override
    public AppointmentRecordDTO getAppointmentRecordDetailByAppointmentId(Long appointmentId) {
        AppointmentRecordDTO record = appointmentRecordRepository
                .findByAppointment_Id(appointmentId)
                .orElseThrow(() -> new HMSException("APPOINTMENT_RECORD_NOT_FOUND"))
                .toDTO();

        // Add prescription details into response
        record.setPrescription(
                prescriptionService.getPrescriptionByAppointmentId(appointmentId)
        );
        return record;
    }

    ///  Get appointment record using record id
    @Override
    public AppointmentRecordDTO getAppointmentRecordById(Long recordId) {

        return appointmentRecordRepository.findById(recordId)
                .orElseThrow(() -> new HMSException("APPOINTMENT_RECORD_NOT_FOUND"))
                .toDTO();
    }

    /// Delete appointment record
    @Override
    public void deleteAppointmentRecord(Long recordId) {
        AppointmentRecord record = appointmentRecordRepository.findById(recordId)
                .orElseThrow(() -> new HMSException("APPOINTMENT_RECORD_NOT_FOUND"));
        try {
            prescriptionService.getPrescriptionByAppointmentId(record.getAppointment().getId());
            prescriptionService.deleteByAppointmentId(record.getAppointment().getId());
        } catch (Exception e) {
            // Ignore exception if prescription does not exist
        }
        appointmentRecordRepository.delete(record);
    }

    /// Get all appointment records for a patient
    @Override
    public List<RecordDetailsDTO> getAppointmentRecordByPatientId(Long patientId) {
        List<AppointmentRecord> records =
                appointmentRecordRepository.findByPatientId(patientId);

        // Convert entity list into DTO list
        List<RecordDetailsDTO> recordDetails = records.stream()
                .map(AppointmentRecord::toRecordDetails)
                .toList();

        // Extract unique doctor ids
        List<Long> doctorsIds = recordDetails.stream()
                .map(RecordDetailsDTO::getDoctorId)
                .distinct()
                .toList();
        List<DoctorName> doctors = profileClients.getDoctorsById(doctorsIds);
        Map<Long, String> doctorMapIdAndName = doctors.stream()
                .collect(Collectors.toMap(
                        DoctorName::getId,
                        DoctorName::getName
                ));
        // Set doctor name into every record
        recordDetails.forEach(record -> {
            String doctorName = doctorMapIdAndName.get(record.getDoctorId());
            if (doctorName != null) {
                record.setDoctorName(doctorName);
            } else {
                record.setDoctorName("Unknown Doctor");
            }
        });
        return recordDetails;
    }
}