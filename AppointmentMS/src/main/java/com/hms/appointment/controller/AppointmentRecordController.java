package com.hms.appointment.controller;

import com.hms.appointment.dto.AppointmentRecordDTO;
import com.hms.appointment.dto.PrescriptionDTO;
import com.hms.appointment.dto.RecordDetailsDTO;
import com.hms.appointment.service.AppointmentRecordService;
import com.hms.appointment.service.PrescriptionPdfService;
import com.hms.appointment.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/appointment/report")
public class AppointmentRecordController {

    private final AppointmentRecordService appointmentRecordService;
    private final PrescriptionService prescriptionService;
    private final PrescriptionPdfService prescriptionPdfService;

    @PostMapping("/create")
    public ResponseEntity<Long> createAppointmentRecord(@RequestBody AppointmentRecordDTO request) {
        return new ResponseEntity<>(appointmentRecordService.createAppointmentRecord(request), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateAppointmentRecord(@RequestBody AppointmentRecordDTO request) {
        appointmentRecordService.updateAppointmentRecord(request);
        return new ResponseEntity<>("Appointment Record Updated", HttpStatus.OK);
    }

    @GetMapping("/getByAppointmentId/{appointmentId}")
    public ResponseEntity<AppointmentRecordDTO> getAppointmentRecordByAppointmentId(@PathVariable("appointmentId") Long appointmentId) {
        return new ResponseEntity<>(appointmentRecordService.getAppointmentRecordByAppointmentId(appointmentId), HttpStatus.OK);
    }

    @GetMapping("/getDetailsByAppointmentId/{appointmentId}")
    public ResponseEntity<AppointmentRecordDTO> getDetailsAppointmentRecordByAppointmentId(@PathVariable("appointmentId") Long appointmentId) {
        return new ResponseEntity<>(appointmentRecordService.getAppointmentRecordDetailByAppointmentId(appointmentId), HttpStatus.OK);
    }

    @GetMapping("/getById/{recordId}")
    public ResponseEntity<AppointmentRecordDTO> getAppointmentRecordById(@PathVariable("recordId") Long recordId) {
        return new ResponseEntity<>(appointmentRecordService.getAppointmentRecordById(recordId), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{recordId}")
    public ResponseEntity<String> deleteAppointmentRecord(@PathVariable Long recordId) {
        appointmentRecordService.deleteAppointmentRecord(recordId);
        return new ResponseEntity<>("Appointment Record Deleted", HttpStatus.OK);
    }

    @GetMapping("/getRecordsByPatientId/{patientId}")
    public ResponseEntity<List<RecordDetailsDTO>> getRecordsByPatientId(@PathVariable Long patientId) {
        return new ResponseEntity<>(appointmentRecordService.getAppointmentRecordByPatientId(patientId), HttpStatus.OK);
    }

    @GetMapping("/prescription/{prescriptionId}/pdf")
    public ResponseEntity<byte[]> generatePrescriptionPdf(@PathVariable Long prescriptionId) {
        PrescriptionDTO prescription = prescriptionService.getPrescriptionById(prescriptionId);

        // Generate real PDF
        byte[] pdf = prescriptionPdfService.generatePrescriptionPdf(prescription);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prescription-" + prescriptionId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }
}
